package woowacourse.shopping.ui.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentItemRepository
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.model.Product
import woowacourse.shopping.ui.cart.component.CartSelection
import woowacourse.shopping.ui.model.mapper.toUiModel
import java.io.IOException

class RecommendViewModel(
    private val cartRepository: CartRepository,
    private val recentItemRepository: RecentItemRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecommendUiState(isLoading = true))
    val uiState: StateFlow<RecommendUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<RecommendEvent>()
    val event: SharedFlow<RecommendEvent> = _event.asSharedFlow()

    private var recommendProducts: List<Product> = emptyList()

    init {
        observeCartItems()
        loadRecommendProducts()
    }

    private fun observeCartItems() {
        viewModelScope.launch {
            combine(
                cartRepository.cartItems,
                cartRepository.selectedCartItemIds,
            ) { cartItems, selectedItems ->
                cartItems to selectedItems
            }.collect { (cartItems, selectedItems) ->
                renderProducts(cartItems, selectedItems)
            }
        }
    }

    private fun loadRecommendProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                cartRepository.refreshCartItems()
                recommendProducts = fetchRecommendProducts()
                renderProducts(
                    cartItems = cartRepository.cartItems.value,
                    selectedItems = cartRepository.selectedCartItemIds.value,
                )
            } catch (_: IOException) {
                _uiState.update { it.copy(errorMessage = "추천 상품을 불러오지 못했습니다.") }
            } catch (_: HttpException) {
                _uiState.update { it.copy(errorMessage = "추천 상품을 불러오지 못했습니다.") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun fetchRecommendProducts(): List<Product> {
        val lastViewedProductId = recentItemRepository.getLastViewedItem()?.id ?: return emptyList()
        val category = productRepository.getProductById(lastViewedProductId).category
        val cartProductIds =
            cartRepository.cartItems.value
                .map { it.product.id }
                .toSet()

        val response =
            productRepository.getProducts(
                category = category,
                page = 0,
                size = 10,
            )

        return response.products
            .filter { product -> product.id !in cartProductIds }
    }

    private fun renderProducts(
        cartItems: List<CartItem>,
        selectedItems: ImmutableList<String>,
    ) {
        val selection = CartSelection(selectedItems).filterSameIds(cartItems)
        val quantityByProductId = cartItems.associate { it.product.id to it.quantity }

        _uiState.update {
            it.copy(
                products =
                    recommendProducts
                        .map { product -> product.toUiModel(quantity = quantityByProductId[product.id]) }
                        .toImmutableList(),
                totalPrice = selection.totalPrice(cartItems).amount,
                totalCount = selection.selectedCount(cartItems),
            )
        }
    }

    fun updateQuantity(
        productId: String,
        quantity: Int,
    ) {
        viewModelScope.launch {
            try {
                cartRepository.setCartItem(productId, quantity)
                cartRepository.cartItems.value
                    .firstOrNull { cartItem -> cartItem.product.id == productId }
                    ?.let { cartItem ->
                        if (quantity > 0) {
                            cartRepository.selectCartItem(cartItem.id)
                        } else {
                            cartRepository.unselectCartItem(cartItem.id)
                        }
                    }
            } catch (_: IOException) {
                _event.emit(RecommendEvent.UpdateCartItemFailure)
            } catch (_: HttpException) {
                _event.emit(RecommendEvent.UpdateCartItemFailure)
            }
        }
    }

    companion object {
        fun provideFactory(
            cartRepository: CartRepository,
            recentItemRepository: RecentItemRepository,
            productRepository: ProductRepository,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    RecommendViewModel(
                        cartRepository = cartRepository,
                        recentItemRepository = recentItemRepository,
                        productRepository = productRepository,
                    )
                }
            }
    }
}
