package woowacourse.shopping.ui.products

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import woowacourse.shopping.common.Event
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.ui.products.adapter.recent.RecentProductUiModel
import woowacourse.shopping.ui.products.adapter.type.ProductUiModel
import kotlin.math.ceil

class ProductsViewModel(
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {
    private val _productsUiState = MutableLiveData<Event<ProductsUiState>>(Event(ProductsUiState()))
    val productsUiState: LiveData<Event<ProductsUiState>> = _productsUiState

    private val _showLoadMore = MutableLiveData<Boolean>(false)
    val showLoadMore: LiveData<Boolean> get() = _showLoadMore

    private var page: Int = INITIALIZE_PAGE
    private var maxPage: Int = INITIALIZE_PAGE

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
        loadMaxPage()
        loadRecentProducts()
    }

    fun loadPage() {
        page = nextPage(page)
    }

    private fun nextPage(page: Int): Int {
        val productsUiState = _productsUiState.value?.peekContent() ?: return page
        _productsUiState.value = Event(productsUiState.copy(isLoading = true))

        runCatching {
            productRepository.findRange(page, PAGE_SIZE)
        }.onSuccess { products ->
            val newProductUiModels = (productUiModels() ?: emptyList()) + products.toProductUiModels()
            _productsUiState.value = Event(ProductsUiState(productUiModels = newProductUiModels))
            _showLoadMore.value = false
            return page + 1
        }.onFailure {
            _productsUiState.value = Event(ProductsUiState(isError = true))
        }
        return page
    }

    fun loadProducts() {
        val productsUiState = _productsUiState.value?.peekContent() ?: return
        _productsUiState.value = Event(productsUiState.copy(isLoading = true))

        runCatching {
            val productUiModels = productUiModels() ?: return
            productUiModels.map { productRepository.find(it.productId) }.toProductUiModels()
        }.onSuccess {
            _productsUiState.value = Event(ProductsUiState(productUiModels = it))
        }.onFailure {
            _productsUiState.value = Event(ProductsUiState(isError = true))
        }
    }

    fun loadProduct(productId: Int) {
        updateProductUiModels(productId)
    }

    private fun List<Product>.toProductUiModels(): List<ProductUiModel> {
        return map { it.toProductUiModel() }
    }

    private fun Product.toProductUiModel(): ProductUiModel {
        return runCatching { cartRepository.find(id) }
            .map { ProductUiModel.from(this, it.quantity) }
            .getOrElse { ProductUiModel.from(this) }
    }

    private fun loadMaxPage() {
        val totalProductCount = productRepository.totalProductCount()
        maxPage = ceil(totalProductCount.toDouble() / PAGE_SIZE).toInt()
    }

    fun loadRecentProducts() {
        _recentProductUiModels.value = recentProductRepository.findRecentProducts().toRecentProductUiModels()
    }

    private fun List<RecentProduct>.toRecentProductUiModels(): List<RecentProductUiModel>? {
        val recentProductsUiModels =
            map {
                val product = productRepository.find(it.productId)
                RecentProductUiModel(product.id, product.imageUrl, product.name)
            }
        return recentProductsUiModels.ifEmpty { null }
    }

    fun changeSeeMoreVisibility(lastPosition: Int) {
        _showLoadMore.value = (lastPosition + 1) % PAGE_SIZE == 0 && lastPosition + 1 == productUiModels()?.size && page < maxPage
    }

    fun decreaseQuantity(productId: Int) {
        cartRepository.decreaseQuantity(productId)
        updateProductUiModels(productId)
    }

    fun increaseQuantity(productId: Int) {
        cartRepository.increaseQuantity(productId)
        updateProductUiModels(productId)
    }

    private fun updateProductUiModels(productId: Int) {
        val productUiModels = productUiModels()?.toMutableList() ?: return
        val newProductUiModel = productRepository.find(productId).toProductUiModel()
        val position = productUiModels.indexOfFirst { it.productId == productId }
        productUiModels[position] = newProductUiModel
        _productsUiState.value = Event(ProductsUiState(productUiModels))
    }

    private fun productUiModels(): List<ProductUiModel>? = _productsUiState.value?.peekContent()?.productUiModels

    companion object {
        private const val INITIALIZE_PAGE = 0
        private const val PAGE_SIZE = 20
    }
}
