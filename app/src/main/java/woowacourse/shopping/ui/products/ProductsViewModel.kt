package woowacourse.shopping.ui.products

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.common.Event
import woowacourse.shopping.domain.model.DataCallback
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
    private val _productsUiState = MutableLiveData<Event<ProductsUiState>>(Event(ProductsUiState()))
    val productsUiState: LiveData<Event<ProductsUiState>> = _productsUiState

    private val _showLoadMore = MutableLiveData<Boolean>(false)
    val showLoadMore: LiveData<Boolean> get() = _showLoadMore

    private val isLastPage = MutableLiveData<Boolean>()

    private var page: Int = INITIALIZE_PAGE

    private val _cartTotalCount: MutableLiveData<Int> = MutableLiveData()
    val cartTotalCount: LiveData<Int> get() = _cartTotalCount

    private val _recentProductUiModels = MutableLiveData<List<RecentProductUiModel>?>()
    val recentProductUiModels: LiveData<List<RecentProductUiModel>?> get() = _recentProductUiModels

    init {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({ loadPage() }, 1000)
        loadRecentProducts()
    }

    fun loadPage() {
        val productsUiState = _productsUiState.value?.peekContent() ?: return
        _productsUiState.value = Event(productsUiState.copy(isLoading = true))
        productRepository.findPage(
            page,
            PAGE_SIZE,
            object : DataCallback<List<Product>> {
                override fun onSuccess(result: List<Product>) {
                    val additionalProductUiModels = result.toProductUiModels()
                    val newProductUiModels =
                        (productUiModels() ?: emptyList()) + additionalProductUiModels
                    _productsUiState.postValue(Event(ProductsUiState(productUiModels = newProductUiModels)))
                    updateTotalCount()
                    _showLoadMore.value = false
                    page++
                }

                override fun onFailure(t: Throwable) {
                    setError()
                }
            },
        )
        loadIsPageLast()
    }

    private fun loadIsPageLast() {
        productRepository.isPageLast(
            page,
            PAGE_SIZE,
            object : DataCallback<Boolean> {
                override fun onSuccess(result: Boolean) {
                    isLastPage.postValue(result)
                }

                override fun onFailure(t: Throwable) {
                    setError()
                }
            },
        )
    }

    fun loadProducts() {
        val productsUiState = _productsUiState.value?.peekContent() ?: return
        _productsUiState.value = Event(productsUiState.copy(isLoading = true))
        val productUiModels = productUiModels()?.toMutableList() ?: return

        productUiModels.forEachIndexed { index, productUiModel ->
            val product = productRepository.syncFind(productUiModel.productId)
            productUiModels[index] = product.toProductUiModel()
        }
        _productsUiState.value = Event(ProductsUiState(productUiModels = productUiModels))
        updateTotalCount()
    }

    fun loadProduct(productId: Int) {
        updateProductUiModel(productId)
    }

    private fun List<Product>.toProductUiModels(): List<ProductUiModel> {
        return map { it.toProductUiModel() }
    }

    private fun Product.toProductUiModel(): ProductUiModel {
        val cartTotalCount = cartRepository.syncGetTotalQuantity()
        val cartItem = cartRepository.syncFindByProductId(id, cartTotalCount)
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
            val product = productRepository.syncFind(it.product.id)
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
        val productUiModel =
            productUiModels()?.find {
                it.productId == productId
            } ?: return
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
        cartRepository.add(
            productId = productId,
            dataCallback =
                object : DataCallback<Unit> {
                    override fun onSuccess(result: Unit) {}

                    override fun onFailure(t: Throwable) {
                        setError()
                    }
                },
        )
    }

    private fun updateProductUiModel(productId: Int) {
        val productUiModels = productUiModels()?.toMutableList() ?: return
        productRepository.find(
            productId,
            object : DataCallback<Product> {
                override fun onSuccess(result: Product) {
                    val newProductUiModel = result.toProductUiModel()
                    val position = productUiModels.indexOfFirst { it.productId == productId }
                    productUiModels[position] = newProductUiModel
                    _productsUiState.postValue(Event(ProductsUiState(productUiModels)))
                    updateTotalCount()
                }

                override fun onFailure(t: Throwable) {
                    setError()
                }
            },
        )
    }

    fun updateTotalCount() {
        cartRepository.getTotalQuantity(
            object : DataCallback<Int> {
                override fun onSuccess(result: Int) {
                    _cartTotalCount.postValue(result)
                }

                override fun onFailure(t: Throwable) {
                    setError()
                }
            },
        )
    }

    private fun updateCartQuantity(productUiModel: ProductUiModel) {
        val cartTotalCount = cartRepository.syncGetTotalQuantity()
        val cartItem = cartRepository.syncFindByProductId(productUiModel.productId, cartTotalCount)

        if (cartItem == null) {
            addCartItem(productUiModel.productId)
            return
        }
        cartRepository.changeQuantity(
            cartItem.id,
            productUiModel.quantity,
            object : DataCallback<Unit> {
                override fun onSuccess(result: Unit) {
                    updateProductUiModel(productUiModel.productId)
                }

                override fun onFailure(t: Throwable) {
                    setError()
                }
            },
        )
    }

    private fun setError() {
        _productsUiState.postValue(Event(ProductsUiState(isError = true)))
    }

    private fun productUiModels(): List<ProductUiModel>? = _productsUiState.value?.peekContent()?.productUiModels

    companion object {
        private const val INITIALIZE_PAGE = 0
        private const val PAGE_SIZE = 20
    }
}
