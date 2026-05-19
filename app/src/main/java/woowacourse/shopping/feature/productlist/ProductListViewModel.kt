package woowacourse.shopping.feature.productlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.repository.cart.CartRepository
import woowacourse.shopping.data.repository.product.ProductRepository
import woowacourse.shopping.data.repository.recentproduct.RecentProductRepository
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.ProductNotFoundException
import woowacourse.shopping.feature.common.state.AppError
import woowacourse.shopping.feature.common.state.ProductUiModel
import woowacourse.shopping.feature.common.state.toAppError

data class ProductListUiState(
    val productUiModels: List<ProductUiModel> = emptyList(),
    val recentProducts: List<ProductUiModel> = emptyList(),
    val mostRecentProductId: String? = null,
    val isLoading: Boolean = true,
    val isEnd: Boolean = false,
    val cartTotalQuantity: Int = 0,
    val error: AppError? = null,
)

class ProductListViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    private var products: List<Product> = emptyList()
    private var cart: Cart = Cart(emptyList())

    fun initialLoading() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                refreshCart()
                fetchAndAppendProducts(20)
                refreshRecentProducts()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.toAppError()) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun loadingFetch() {
        viewModelScope.launch {
            try {
                fetchAndAppendProducts(20)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.toAppError()) }
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    fun increase(productId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val contentId = cart.cartContents.firstOrNull { it.hasProductId(productId) }?.id
            if (contentId == null) {
                cartRepository.insert(productId)
            } else {
                val nextQuantity = cart.quantityOf(productId) + 1
                cartRepository.updateQuantity(contentId, nextQuantity)
            }
            refreshCart()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun decrease(productId: String) {
        products.firstOrNull { it.id == productId }
            ?: throw ProductNotFoundException(productId)

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val contentId = cart.cartContents.firstOrNull { it.hasProductId(productId) }?.id
            if (contentId != null) {
                cartRepository.decrease(contentId)
            }
            refreshCart()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun insertRecentProduct(productId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            recentProductRepository.insert(productId)
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun loadRecentProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            refreshRecentProducts()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun cartRefresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            refreshCart()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun refreshCart() {
        cart = cartRepository.loadCart()

        _uiState.update {
            it.copy(
                cartTotalQuantity = cart.totalQuantityOf(),
                productUiModels = products.map(::toProductUiModel),
            )
        }
    }

    private suspend fun fetchAndAppendProducts(pageSize: Int) {
        val page = productRepository.loadProducts(
            page = products.size / pageSize,
            pageSize = pageSize,
            sort = emptyList(),
            category = null,
        )
        products = products + page.products

        _uiState.update {
            it.copy(
                productUiModels = products.map(::toProductUiModel),
                isEnd = page.isLast,
            )
        }
    }

    private suspend fun refreshRecentProducts() {
        val recentProductIds = recentProductRepository.loadProducts()
        val productById = products.associateBy { it.id }
        val recents = recentProductIds.mapNotNull { productById[it] }
        if (recents.isEmpty()) return

        _uiState.update {
            it.copy(
                recentProducts = recents.map(::toProductUiModel),
                mostRecentProductId = recents.first().id,
            )
        }
    }

    private fun toProductUiModel(product: Product): ProductUiModel {
        return ProductUiModel(
            name = product.name,
            price = product.priceAmount(),
            imageUrl = product.imageUrl,
            id = product.id,
            quantity = cart.quantityOf(product.id),
        )
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as ShoppingApplication
                ProductListViewModel(
                    app.cartRepository,
                    app.productRepository,
                    app.recentProductRepository,
                )
            }
        }
    }
}
