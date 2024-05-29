package woowacourse.shopping.ui.products

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import woowacourse.shopping.common.Event
import woowacourse.shopping.data.product.remote.retrofit.DataCallback
import woowacourse.shopping.data.product.remote.retrofit.RemoteProductRepository
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.ui.products.adapter.recent.RecentProductUiModel
import woowacourse.shopping.ui.products.adapter.type.ProductUiModel
import java.util.concurrent.CountDownLatch

class ProductsViewModel(
    private val productRepository: RemoteProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _productsUiState = MutableLiveData<Event<ProductsUiState>>(Event(ProductsUiState()))
    val productsUiState: LiveData<Event<ProductsUiState>> = _productsUiState

    private val _showLoadMore = MutableLiveData<Boolean>(false)
    val showLoadMore: LiveData<Boolean> get() = _showLoadMore

    private var page: Int = INITIALIZE_PAGE

    val cartTotalCount: LiveData<Int> =
        _productsUiState.map {
            val productsUiModels = productUiModels() ?: return@map 0
            productsUiModels.fold(0) { acc, productUiModel -> acc + productUiModel.quantity.count }
        }

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
        productRepository.findProducts(
            page,
            PAGE_SIZE,
            object : DataCallback<List<Product>> {
                override fun onSuccess(result: List<Product>) {
                    val additionalProductUiModels = result.toProductUiModels()
                    val newProductUiModels =
                        (productUiModels() ?: emptyList()) + additionalProductUiModels
                    _productsUiState.postValue(Event(ProductsUiState(productUiModels = newProductUiModels)))
                    _showLoadMore.value = false
                    page++
                    // maxPage = body.totalPages
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
        runCatching {
            cartRepository.findAll()
        }.onSuccess { cartItems ->
            val productUiModels = productUiModels()?.toMutableList() ?: return
            cartItems.forEach { cartItem ->
                val productId = cartItem.productId
                val index =
                    productUiModels.indexOfFirst { productUiModel ->
                        productId == productUiModel.productId
                    }
                productUiModels[index] = productUiModels[index].copy(quantity = cartItem.quantity)
            }
            _productsUiState.value = Event(ProductsUiState(productUiModels = productUiModels))
        }.onFailure {
            _productsUiState.value = Event(ProductsUiState(isError = true))
        }
    }

    fun loadProduct(productId: Int) {
        updateProductUiModel(productId)
    }

    private fun List<Product>.toProductUiModels(): List<ProductUiModel> {
        return map { it.toProductUiModel() }
    }

    private fun Product.toProductUiModel(): ProductUiModel {
        return runCatching { cartRepository.find(id) }
            .map { ProductUiModel.from(this, it.quantity) }
            .getOrElse { ProductUiModel.from(this) }
    }

    fun loadRecentProducts() {
        _recentProductUiModels.value =
            recentProductRepository.findRecentProducts().toRecentProductUiModels()
    }

    private fun List<RecentProduct>.toRecentProductUiModels(): List<RecentProductUiModel>? {
        val recentProductsUiModels: MutableList<RecentProductUiModel> = mutableListOf()
        val latch = CountDownLatch(this.size)
        this.forEach {
            productRepository.find(
                it.productId,
                object : DataCallback<Product> {
                    override fun onSuccess(product: Product) {
                        recentProductsUiModels.add(
                            RecentProductUiModel(
                                product.id,
                                product.imageUrl,
                                product.name,
                            ),
                        )
                        latch.countDown()
                    }

                    override fun onFailure(t: Throwable) {
                        latch.countDown()
                    }
                },
            )
        }

        latch.await()
        return recentProductsUiModels.ifEmpty { null }
    }

    fun changeSeeMoreVisibility(lastPosition: Int) {
        _showLoadMore.value =
            (lastPosition + 1) % PAGE_SIZE == 0 && lastPosition + 1 == productUiModels()?.size
    }

    fun decreaseQuantity(productId: Int) {
        cartRepository.decreaseQuantity(productId)
        updateProductUiModel(productId)
    }

    fun increaseQuantity(productId: Int) {
        cartRepository.increaseQuantity(productId)
        updateProductUiModel(productId)
    }

    private fun updateProductUiModel(productId: Int) {
        // loading
        val productUiModels = productUiModels()?.toMutableList() ?: return
        productRepository.find(
            productId,
            object : DataCallback<Product> {
                override fun onSuccess(result: Product) {
                    val newProductUiModel = result.toProductUiModel()
                    val position = productUiModels.indexOfFirst { it.productId == productId }
                    productUiModels[position] = newProductUiModel
                    _productsUiState.postValue(Event(ProductsUiState(productUiModels)))
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
