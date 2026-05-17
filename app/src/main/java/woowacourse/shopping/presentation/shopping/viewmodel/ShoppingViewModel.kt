package woowacourse.shopping.presentation.shopping.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException
import woowacourse.shopping.di.RepositoryProvider
import woowacourse.shopping.domain.addToCartUseCase
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.presentation.common.model.toUiModel
import woowacourse.shopping.presentation.shopping.model.ShoppingItemUiModel
import woowacourse.shopping.presentation.shopping.model.ShoppingUiState

class ShoppingViewModel(
    private val productRepository: ProductRepository = RepositoryProvider.productRepository,
    private val cartRepository: CartRepository = RepositoryProvider.cartRepository,
) : ViewModel() {
    private val cart = cartRepository.cart
    private val products = productRepository.products
    private val recentProducts =
        productRepository
            .getRecentProductsStream(RECENT_PRODUCT_SIZE)
            .map { products ->
                products.map { product -> product.toUiModel() }
            }

    private val _uiState = MutableStateFlow(ShoppingUiState())

    val uiState: StateFlow<ShoppingUiState> =
        combine(
            _uiState,
            products,
            cart,
            recentProducts,
        ) { shoppingUiState, products, cart, recentProducts ->
            shoppingUiState.copy(
                products =
                    products.map { product ->
                        ShoppingItemUiModel(
                            product = product.toUiModel(),
                            quantity = cart[product.id]?.quantity ?: 0,
                        )
                    },
                totalQuantity = cart.totalQuantity,
                recentProducts = recentProducts,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = _uiState.value,
        )

    init {
        viewModelScope.launch {
            cartRepository.loadCart()
            loadProducts(
                offset = _uiState.value.offset,
                limit = PRODUCT_PAGE_SIZE,
            )
        }
    }

    fun addItemToCart(id: Long) {
        viewModelScope.launch {
            addToCartUseCase(cartRepository, id)
        }
    }

    fun removeItemFromCart(id: Long) {
        viewModelScope.launch {
            val cartItem =
                cart.value.items.find { it.product.id == id } ?: return@launch
            cartRepository.changeCartItem(id, cartItem.decrease().quantity)
        }
    }

    fun upsertRecentProduct(id: Long) {
        viewModelScope.launch {
            productRepository.upsertRecentProduct(id)
        }
    }

    fun loadMoreProducts() {
        viewModelScope.launch {
            loadProducts(_uiState.value.offset, PRODUCT_PAGE_SIZE)
        }
    }

    private suspend fun loadProducts(
        offset: Int,
        limit: Int,
    ) {
        if (_uiState.value.isLoading || !_uiState.value.canLoadMore) return
        _uiState.update {
            it.copy(isLoading = true)
        }
        try {
            val loadedSize =
                productRepository.loadProducts(
                    offset = offset,
                    limit = limit,
                )
            _uiState.update {
                it.copy(
                    offset = it.offset + loadedSize,
                    canLoadMore = loadedSize == limit,
                )
            }
        } catch (_: IOException) {
            _uiState.update { it.copy(errorMessage = "네트워크 연결을 확인해주세요.") }
        } finally {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    companion object {
        private const val RECENT_PRODUCT_SIZE = 10
        private const val PRODUCT_PAGE_SIZE = 20
    }
}
