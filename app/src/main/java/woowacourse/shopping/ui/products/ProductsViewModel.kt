package woowacourse.shopping.ui.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.common.Event
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
    private val _productUiModels = MutableLiveData<List<ProductUiModel>>()
    val productUiModels: LiveData<List<ProductUiModel>> get() = _productUiModels

    private val _isLoadingProducts = MutableLiveData<Boolean>()
    val isLoadingProducts: LiveData<Boolean> get() = _isLoadingProducts

    private val _productsErrorEvent = MutableLiveData<Event<Unit>>()
    val productsErrorEvent: LiveData<Event<Unit>> get() = _productsErrorEvent

    private val _showLoadMore = MutableLiveData<Boolean>(false)
    val showLoadMore: LiveData<Boolean> get() = _showLoadMore

    private val isLastPage = MutableLiveData<Boolean>()

    private var page: Int = INITIALIZE_PAGE

    private val _cartTotalCount: MutableLiveData<Int> = MutableLiveData()
    val cartTotalCount: LiveData<Int> get() = _cartTotalCount

    private val _recentProductUiModels = MutableLiveData<List<RecentProductUiModel>?>()
    val recentProductUiModels: LiveData<List<RecentProductUiModel>?> get() = _recentProductUiModels

    init {
        loadPage()
        loadRecentProducts()
    }

    fun loadPage() {
        _isLoadingProducts.value = true
        productRepository.findPage(page, PAGE_SIZE) {
            it.onSuccess { products ->
                _isLoadingProducts.value = false
                val additionalProductUiModels = products.toProductUiModels()
                val newProductUiModels = (productUiModels() ?: emptyList()) + additionalProductUiModels
                _productUiModels.value = newProductUiModels
                updateTotalCount()
                _showLoadMore.value = false
                page++
            }.onFailure {
                _isLoadingProducts.value = false
                setError()
            }
        }
        loadIsPageLast()
    }

    private fun loadIsPageLast() {
        productRepository.isLastPage(page, PAGE_SIZE) {
            it.onSuccess { isLastPage ->
                this.isLastPage.value = isLastPage
            }.onFailure {
                setError()
            }
        }
    }

    fun loadProducts() {
        _isLoadingProducts.value = true
        val productUiModels = productUiModels()?.toMutableList() ?: return

        productUiModels.forEachIndexed { index, productUiModel ->
            val product = productRepository.syncFind(productUiModel.productId) ?: return@forEachIndexed
            productUiModels[index] = product.toProductUiModel()
        }
        _isLoadingProducts.value = false
        _productUiModels.value = productUiModels
        updateTotalCount()
    }

    fun loadProduct(productId: Int) {
        updateProductUiModel(productId)
    }

    private fun List<Product>.toProductUiModels(): List<ProductUiModel> {
        return map { it.toProductUiModel() }
    }

    private fun Product.toProductUiModel(): ProductUiModel {
        val cartItem = cartRepository.syncFindByProductId(id)
        return if (cartItem == null) {
            ProductUiModel.from(this@toProductUiModel)
        } else {
            ProductUiModel.from(this@toProductUiModel, cartItem.quantity)
        }
    }

    fun loadRecentProducts() {
        _recentProductUiModels.value =
            recentProductRepository.findRecentProducts().toRecentProductUiModels()
    }

    private fun List<RecentProduct>.toRecentProductUiModels(): List<RecentProductUiModel>? {
        return map {
            val product = productRepository.syncFind(it.product.id) ?: return null
            RecentProductUiModel(
                product.id,
                product.imageUrl,
                product.name,
            )
        }.ifEmpty { null }
    }

    fun changeSeeMoreVisibility(lastPosition: Int) {
        _showLoadMore.value =
            (lastPosition + 1) % PAGE_SIZE == 0 && lastPosition + 1 == productUiModels()?.size && isLastPage.value == false
    }

    fun decreaseQuantity(productId: Int) {
        val productUiModel = productUiModels()?.find { it.productId == productId } ?: return
        val newProductUiModel = productUiModel.copy(quantity = productUiModel.quantity.dec())
        updateCartQuantity(newProductUiModel)
        updateProductUiModel(productId)
    }

    fun increaseQuantity(productId: Int) {
        val productUiModel = productUiModels()?.find { it.productId == productId } ?: return
        val newProductUiModel = productUiModel.copy(quantity = productUiModel.quantity.inc())
        updateCartQuantity(newProductUiModel)
        updateProductUiModel(productId)
    }

    private fun addCartItem(productId: Int) {
        cartRepository.add(productId) {
            it.onSuccess { }
                .onFailure { setError() }
        }
    }

    private fun updateProductUiModel(productId: Int) {
        val productUiModels = productUiModels()?.toMutableList() ?: return
        productRepository.find(productId) {
            it.onSuccess { product ->
                val newProductUiModel = product.toProductUiModel()
                val position = productUiModels.indexOfFirst { it.productId == productId }
                productUiModels[position] = newProductUiModel
                _productUiModels.value = productUiModels
                updateTotalCount()
            }.onFailure {
                setError()
            }
        }
    }

    private fun updateTotalCount() {
        cartRepository.getTotalQuantity {
            it.onSuccess { totalCount ->
                _cartTotalCount.value = totalCount
            }.onFailure {
                setError()
            }
        }
    }

    private fun updateCartQuantity(productUiModel: ProductUiModel) {
        val cartItem = cartRepository.syncFindByProductId(productUiModel.productId)

        if (cartItem == null) {
            addCartItem(productUiModel.productId)
            return
        }
        cartRepository.changeQuantity(cartItem.id, productUiModel.quantity) {
            it.onSuccess {
                updateProductUiModel(productUiModel.productId)
            }.onFailure {
                setError()
            }
        }
    }

    private fun setError() {
        _productsErrorEvent.value = Event(Unit)
    }

    private fun productUiModels(): List<ProductUiModel>? = _productUiModels.value

    companion object {
        private const val INITIALIZE_PAGE = 0
        private const val PAGE_SIZE = 20
    }
}
