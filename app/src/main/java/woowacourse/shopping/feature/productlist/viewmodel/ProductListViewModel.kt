package woowacourse.shopping.feature.productlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.constants.MockData
import woowacourse.shopping.data.repository.cart.CartRepository
import woowacourse.shopping.data.repository.product.ProductRepository
import woowacourse.shopping.data.repository.recentproduct.RecentProductRepository
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.ProductNotFoundException
import woowacourse.shopping.feature.common.state.ProductUiModel

sealed interface ProductListEvent {
    data class FatalError(
        val message: String,
    ) : ProductListEvent
}

data class ProductListUiState(
    val productUiModels: List<ProductUiModel> = emptyList(),
    val recentProducts: List<ProductUiModel> = emptyList(),
    val mostRecentProductId: Long? = null,
    val isLoading: Boolean = true,
    val isEnd: Boolean = false,
    val cartTotalQuantity: Int = 0,
)

class ProductListViewModel(
    private val application: ShoppingApplication
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    private val _event = Channel<ProductListEvent>(Channel.BUFFERED)
    val event: Flow<ProductListEvent> = _event.receiveAsFlow()

    lateinit var productRepository: ProductRepository
    lateinit var cartRepository: CartRepository
    lateinit var recentProductRepository: RecentProductRepository

    init {
        viewModelScope.launch {
            val appDependencies = application.appDependenciesDeferred.await()
            productRepository = appDependencies.productRepository
            cartRepository = appDependencies.cartRepository
            recentProductRepository = appDependencies.recentProductRepository
        }
        initialLoading()
    }

    private var products: List<Product> = emptyList()
    private var cart: Cart = Cart(emptyList())
    fun initialLoading() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            refreshCart()
            fetchAndAppendProducts(20)
            refreshRecentProducts()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun loadingFetch() {
        viewModelScope.launch {
            fetchAndAppendProducts(20)
        }
    }

    fun increase(productId: Long) = guardFatal {
        val product = products.firstOrNull { it.id == productId }
            ?: throw ProductNotFoundException(productId)

        viewModelScope.launch {
            cartRepository.increase(product)
            refreshCart()
        }
    }

    fun decrease(productId: Long) = guardFatal {
        products.firstOrNull { it.id == productId }
            ?: throw ProductNotFoundException(productId)

        viewModelScope.launch {
            cartRepository.decrease(productId)
            refreshCart()
        }
    }

    private inline fun guardFatal(block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            _event.trySend(
                ProductListEvent.FatalError(
                    e.message
                        ?: "알 수 없는 오류가 발생했습니다.",
                ),
            )
        }
    }

    fun insertRecentProduct(productId: Long) {
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
        val (result, isEnd) = productRepository.loadProducts(
            startIndex = products.size / pageSize,
            pageSize = pageSize,
            sort = emptyList(),
            category = null
        )
        products = products + result
        _uiState.update {
            it.copy(
                productUiModels = products.map(::toProductUiModel),
                isEnd = isEnd,
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

    fun toProductUiModel(product: Product): ProductUiModel {
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
                    app
                )
            }
        }
    }
}
