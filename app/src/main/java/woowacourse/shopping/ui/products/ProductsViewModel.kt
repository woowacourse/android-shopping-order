package woowacourse.shopping.ui.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import woowacourse.shopping.common.MutableSingleLiveData
import woowacourse.shopping.common.SingleLiveData
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.ui.products.adapter.recent.RecentProductUiModel
import woowacourse.shopping.ui.products.adapter.type.ProductUiModel

class ProductsViewModel(
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _productUiModels = MutableLiveData<List<ProductUiModel>>(emptyList())
    val productUiModels: LiveData<List<ProductUiModel>> get() = _productUiModels

    private val _isLoadingProducts = MutableLiveData<Boolean>()
    val isLoadingProducts: LiveData<Boolean> get() = _isLoadingProducts

    private val _showLoadMore = MutableLiveData<Boolean>(false)
    val showLoadMore: LiveData<Boolean> get() = _showLoadMore

    private val isLastPage = MutableLiveData<Boolean>()

    private var page: Int = INITIALIZE_PAGE

    private val _cartTotalQuantity: MutableLiveData<Int> = MutableLiveData()
    val cartTotalQuantity: LiveData<Int> get() = _cartTotalQuantity

    private val _recentProductUiModels = MutableLiveData<List<RecentProductUiModel>?>()
    val recentProductUiModels: LiveData<List<RecentProductUiModel>?> get() = _recentProductUiModels

    private val _productsLoadError = MutableSingleLiveData<Throwable>()
    val productsLoadError: SingleLiveData<Throwable> get() = _productsLoadError

    private val _cartItemAddError = MutableSingleLiveData<Throwable>()
    val cartItemAddError: SingleLiveData<Throwable> get() = _cartItemAddError

    private val _cartItemDeleteError = MutableSingleLiveData<Throwable>()
    val cartItemDeleteError: SingleLiveData<Throwable> get() = _cartItemDeleteError

    init {
        loadPage()
        loadRecentProducts()
    }

    fun loadPage() =
        viewModelScope.launch {
            _isLoadingProducts.value = true
            productRepository.findPage(page, PAGE_SIZE)
                .onSuccess { products ->
                    updateProductUiModels(products)
                    loadIsPageLast()
                    _showLoadMore.value = false
                    page++
                }.onFailure {
                    _productsLoadError.setError(it)
                }
            _isLoadingProducts.value = false
        }

    private fun updateProductUiModels(products: List<Product>) =
        viewModelScope.launch {
            val additionalProductUiModels = products.toProductUiModels(this)
            val newProductUiModels = productUiModels() + additionalProductUiModels
            _productUiModels.value = newProductUiModels
            updateTotalCount()
        }

    private suspend fun List<Product>.toProductUiModels(scope: CoroutineScope): List<ProductUiModel> {
        val productUiModelsDeferred = scope.async { map { it.toProductUiModel(scope) } }
        return productUiModelsDeferred.await()
    }

    private suspend fun Product.toProductUiModel(scope: CoroutineScope): ProductUiModel {
        val cartItemDeferred =
            scope.async {
                cartRepository.findByProductId(id).getOrNull()
            }
        val cartItem = cartItemDeferred.await()
        return if (cartItem == null) {
            ProductUiModel.from(this@toProductUiModel)
        } else {
            ProductUiModel.from(this@toProductUiModel, cartItem.quantity)
        }
    }

    private fun loadIsPageLast() =
        viewModelScope.launch {
            productRepository.isLastPage(page, PAGE_SIZE)
                .onSuccess { isLastPage ->
                    this@ProductsViewModel.isLastPage.value = isLastPage
                }.onFailure {
                    _productsLoadError.setError(it)
                }
        }

    fun loadProducts() =
        viewModelScope.launch {
            _isLoadingProducts.value = true
            val productUiModels = productUiModels().toMutableList()
            productUiModels.forEachIndexed { index, productUiModel ->
                val product = productRepository.find(productUiModel.productId).getOrNull() ?: return@launch
                productUiModels[index] = product.toProductUiModel(this)
            }
            _isLoadingProducts.value = false
            _productUiModels.value = productUiModels
            updateTotalCount()
        }

    fun loadProduct(productId: Int) {
        updateProductUiModel(productId)
    }

    fun loadRecentProducts() =
        viewModelScope.launch {
            val recentProducts = recentProductRepository.findRecentProducts().getOrNull() ?: return@launch
            _recentProductUiModels.value = recentProducts.toRecentProductUiModels(this)
        }

    private suspend fun List<RecentProduct>.toRecentProductUiModels(scope: CoroutineScope): List<RecentProductUiModel>? {
        val recentProductUiModelsDeferred =
            scope.async {
                map {
                    val product = productRepository.find(it.product.id).getOrNull() ?: return@async null
                    RecentProductUiModel(product.id, product.imageUrl, product.name)
                }
            }
        return recentProductUiModelsDeferred.await()?.ifEmpty { null }
    }

    fun changeSeeMoreVisibility(lastPosition: Int) {
        _showLoadMore.value =
            (lastPosition + 1) % PAGE_SIZE == 0 && lastPosition + 1 == productUiModels().size && isLastPage.value == false
    }

    fun increaseQuantity(productId: Int) {
        val productUiModel = productUiModels().find { it.productId == productId } ?: return
        val newProductUiModel = productUiModel.copy(quantity = productUiModel.quantity.inc())
        updateCartQuantity(newProductUiModel, _cartItemAddError)
    }

    fun decreaseQuantity(productId: Int) {
        val productUiModel = productUiModels().find { it.productId == productId } ?: return
        val newProductUiModel = productUiModel.copy(quantity = productUiModel.quantity.dec())
        updateCartQuantity(newProductUiModel, _cartItemDeleteError)
    }

    private fun updateCartQuantity(
        productUiModel: ProductUiModel,
        errorEvent: MutableSingleLiveData<Throwable>,
    ) = viewModelScope.launch {
        val cartItem = cartRepository.findByProductId(productUiModel.productId).getOrNull()
        if (cartItem == null) {
            addCartItem(productUiModel.productId)
        } else if (productUiModel.quantity.isMin()) {
            deleteCartItem(cartItem)
        } else {
            cartRepository.changeQuantity(cartItem.id, productUiModel.quantity)
                .onFailure { errorEvent.setError(it) }
        }

        updateProductUiModel(productUiModel.productId)
    }

    private fun updateProductUiModel(productId: Int) =
        viewModelScope.launch {
            val productUiModels = productUiModels().toMutableList()
            productRepository.find(productId)
                .onSuccess { product ->
                    viewModelScope.launch {
                        val newProductUiModel = product.toProductUiModel(this)
                        val position = productUiModels.indexOfFirst { productUiModel -> productUiModel.productId == productId }
                        productUiModels[position] = newProductUiModel
                        _productUiModels.value = productUiModels
                        updateTotalCount()
                    }
                }.onFailure {
                    _productsLoadError.setError(it)
                }
        }

    private fun updateTotalCount() =
        viewModelScope.launch {
            cartRepository.getTotalQuantity()
                .onSuccess { _cartTotalQuantity.value = it }
                .onFailure { _productsLoadError.setError(it) }
        }

    private fun addCartItem(productId: Int) =
        viewModelScope.launch {
            cartRepository.add(productId)
                .onFailure { _cartItemAddError.setError(it) }
        }

    private fun deleteCartItem(cartItem: CartItem) =
        viewModelScope.launch {
            cartRepository.delete(cartItem.id)
                .onFailure { _cartItemDeleteError.setError(it) }
        }

    private fun MutableSingleLiveData<Throwable>.setError(throwable: Throwable) {
        this.setValue(throwable)
    }

    private fun productUiModels(): List<ProductUiModel> = _productUiModels.value ?: emptyList()

    companion object {
        private const val INITIALIZE_PAGE = 0
        private const val PAGE_SIZE = 20
    }
}
