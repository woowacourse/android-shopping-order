package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentItemRepository
import woowacourse.shopping.model.Product
import woowacourse.shopping.ui.model.mapper.toUiModel
import kotlin.coroutines.cancellation.CancellationException

class CartViewModel(
    private val cartRepository: CartRepository,
    private val recentItemRepository: RecentItemRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                getCartItemsByPage()
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun getCartItemsByPage(targetPage: Int = uiState.value.page) {
        try {
            val cartResult =
                cartRepository.getCartItemsByPage(page = targetPage, size = PAGE_SIZE)

            val totalPrice =
                cartRepository.getTotalPrice(uiState.value.selectedCartItems)

            _uiState.update {
                it.copy(
                    page = targetPage,
                    items =
                        cartResult.cartItems
                            .map { cartItem ->
                                cartItem.toUiModel(isSelected(cartItem.id))
                            }.toImmutableList(),
                    isCanMoveNext = !cartResult.isLastPage,
                    totalCartCount = cartRepository.getCartItemsCount(),
                    totalCartQuantity = cartRepository.getTotalCartItemQuantity(),
                    totalPrice = totalPrice.amount,
                    recommendProducts =
                        loadRecommendProducts()
                            .map { recentProduct ->
                                recentProduct.toUiModel(
                                    quantity = cartRepository.getCartItemQuantity(recentProduct.id),
                                )
                            }.toImmutableList(),
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            _uiState.update {
                it.copy(errorMessage = "카트 정보를 불러오는 데 실패했습니다.")
            }
        }
    }

    fun nextPage() {
        viewModelScope.launch {
            getCartItemsByPage(targetPage = uiState.value.page + 1)
        }
    }

    fun previousPage() {
        viewModelScope.launch {
            getCartItemsByPage(targetPage = uiState.value.page - 1)
        }
    }

    fun deleteItem(cartId: Long) {
        viewModelScope.launch {
            try {
                cartRepository.deleteItem(cartId)
                getCartItemsByPage()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { it.copy(errorMessage = "상품 삭제에 실패했습니다.") }
            }
        }
    }

    fun updateQuantity(
        productId: Long,
        quantity: Int,
    ) {
        viewModelScope.launch {
            try {
                cartRepository.setCartItem(productId, quantity = quantity)
                getCartItemsByPage()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { it.copy(errorMessage = "수량 변경에 실패했습니다.") }
            }
        }
    }

    fun checkItem(cartItemId: Long) {
        val selectedItems =
            if (_uiState.value.selectedCartItems.contains(cartItemId)) {
                _uiState.value.selectedCartItems - cartItemId
            } else {
                _uiState.value.selectedCartItems + cartItemId
            }.toImmutableList()

        _uiState.update {
            it.copy(
                selectedCartItems = selectedItems,
            )
        }

        viewModelScope.launch {
            getCartItemsByPage()
        }
    }

    private fun isSelected(cartItemId: Long): Boolean = _uiState.value.selectedCartItems.contains(cartItemId)

    fun isAllSelectClick() {
        val selectedItems =
            if (_uiState.value.isAllChecked) {
                emptyList()
            } else {
                _uiState.value.items.map { it.id }
            }.toImmutableList()

        _uiState.update {
            it.copy(
                selectedCartItems = selectedItems,
                isAllChecked = !it.isAllChecked,
                selectedCartItemCount = selectedItems.size,
            )
        }

        viewModelScope.launch {
            getCartItemsByPage()
        }
    }

    suspend fun loadRecommendProducts(): List<Product> {
        val productId = recentItemRepository.getLastViewedItem()?.id

        return productId?.let {
            val category = productRepository.getProductById(productId).category
            productRepository
                .getProducts(
                    category = category,
                    page = 0,
                    size = 10,
                ).products
        } ?: emptyList()
    }

    fun setOrder() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isOrder = true,
                    recommendProducts =
                        loadRecommendProducts()
                            .filter { product ->
                                cartRepository.getCartItemQuantity(product.id) == null
                            }.map { product ->
                                product
                                    .toUiModel(quantity = cartRepository.getCartItemQuantity(product.id))
                            }.toImmutableList(),
                )
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 5

        fun provideFactory(
            cartRepository: CartRepository,
            recentItemRepository: RecentItemRepository,
            productRepository: ProductRepository,
        ): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    CartViewModel(
                        cartRepository = cartRepository,
                        recentItemRepository = recentItemRepository,
                        productRepository = productRepository,
                    )
                }
            }
    }
}
