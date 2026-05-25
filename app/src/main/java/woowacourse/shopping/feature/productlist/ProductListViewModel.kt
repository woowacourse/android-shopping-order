package woowacourse.shopping.feature.productlist

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
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.repository.cart.CartRepository
import woowacourse.shopping.data.repository.product.ProductRepository
import woowacourse.shopping.data.repository.recentproduct.RecentProductRepository
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.Product
import woowacourse.shopping.feature.common.state.ProductUiModel

class ProductListViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    private val _event = Channel<ProductListEvent>(Channel.BUFFERED)
    val event: Flow<ProductListEvent> = _event.receiveAsFlow()

    init {
        viewModelScope.launch {
            initialLoading()
        }
    }

    private var products: List<Product> = emptyList()
    private var cart: Cart = Cart(emptyList())

    fun initialLoading() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                refreshCart()
                fetchAndAppendProducts(20)
                refreshRecentProducts()
            } catch (e: HttpException) {
                _event.send(ProductListEvent.FatalError("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."))
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
                _event.send(ProductListEvent.FatalError("추가 상품을 불러오는데 실패했습니다."))
            }
        }
    }

    fun increase(productId: Long) {
        viewModelScope.launch {
            try {
                if (products.none { it.id == productId }) return@launch
                val product = products.first { it.id == productId }
                cartRepository.increase(product)
                refreshCart()
            } catch (e: Exception) {
                _event.send(ProductListEvent.FatalError("장바구니 담기에 실패했습니다."))
            }
        }
    }

    fun decrease(productId: Long) {
        viewModelScope.launch {
            try {
                cartRepository.decrease(productId)
                refreshCart()
            } catch (e: Exception) {
                _event.send(ProductListEvent.FatalError("수량 변경에 실패했습니다."))
            }
        }
    }

    fun insertRecentProduct(productId: Long) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                recentProductRepository.insert(productId)
            } catch (e: Exception) {
                _event.send(ProductListEvent.FatalError("최근 본 상품에 추가하는데 실패했습니다."))
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun loadRecentProducts() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                refreshRecentProducts()
            } catch (e: Exception) {
                _event.send(ProductListEvent.FatalError("최근 본 상품을 불러오는데 실패했습니다."))
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun cartRefresh() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                refreshCart()
            } catch (e: Exception) {
                _event.send(ProductListEvent.FatalError("장바구니 정보를 갱신하는데 실패했습니다."))
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
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
        val (result, isEnd) =
            productRepository.loadProducts(
                startIndex = products.size / pageSize,
                pageSize = pageSize,
                sort = emptyList(),
                category = null,
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

    fun toProductUiModel(product: Product): ProductUiModel =
        ProductUiModel(
            name = product.name,
            price = product.priceAmount(),
            imageUrl = product.imageUrl,
            id = product.id,
            quantity = cart.quantityOf(product.id),
        )

    companion object {
        val Factory =
            viewModelFactory {
                initializer {
                    val app = this[APPLICATION_KEY] as ShoppingApplication
                    val appDependencies = runBlocking { app.appDependenciesDeferred.await() }
                    ProductListViewModel(
                        productRepository = appDependencies.productRepository,
                        cartRepository = appDependencies.cartRepository,
                        recentProductRepository = appDependencies.recentProductRepository,
                    )
                }
            }
    }
}

sealed interface ProductListEvent {
    data class FatalError(
        val message: String,
    ) : ProductListEvent
}
