package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.ProductRepository
import woowacourse.shopping.data.repository.RecentItemRepository
import woowacourse.shopping.model.Product
import woowacourse.shopping.ui.model.mapper.toUiModel

class CartViewModel(
    private val cartRepository: CartRepository,
    private val recentItemRepository: RecentItemRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<CartUiEvent>()
    val uiEvent: SharedFlow<CartUiEvent> = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        uiInfoState =
                            it.uiInfoState.copy(
                                isLoading = true,
                            ),
                    )
                }

                getCartItemsByPage()
            } finally {
                _uiState.update {
                    it.copy(
                        uiInfoState =
                            it.uiInfoState.copy(
                                isLoading = false,
                            ),
                    )
                }
            }
        }
    }

    private suspend fun getCartItemsByPage(page: Int = uiState.value.pageState.page) {
        val cartResult =
            cartRepository.getCartItemsByPage(page = page, size = PAGE_SIZE)

        val totalPrice =
            cartRepository.getTotalPrice(uiState.value.selectedCartState.selectedCartItems)

        _uiState.update {
            it.copy(
                items =
                    cartResult.cartItems
                        .map { cartItem ->
                            cartItem.toUiModel(isSelected(cartItem.id))
                        }.toImmutableList(),
                pageState =
                    it.pageState.copy(
                        isCanMoveNext = !cartResult.isLastPage,
                    ),
                cartSummary =
                    it.cartSummary.copy(
                        totalCartCount = cartRepository.getCartItemsCount(),
                        totalCartQuantity = cartRepository.getTotalCartItemQuantity(),
                        totalPrice = totalPrice.amount,
                    ),
                recommendProducts =
                    loadRecommendProducts()
                        .map { recentProduct ->
                            recentProduct.toUiModel(
                                quantity = cartRepository.getCartItemQuantity(recentProduct.id),
                            )
                        }.toImmutableList(),
            )
        }
    }

    fun nextPage() {
        viewModelScope.launch {
            val page = uiState.value.pageState.page + 1

            getCartItemsByPage(page)

            _uiState.update {
                it.copy(
                    pageState =
                        it.pageState.copy(
                            page = page,
                        ),
                )
            }
        }
    }

    fun previousPage() {
        viewModelScope.launch {
            val page = uiState.value.pageState.page - 1

            getCartItemsByPage(page)

            _uiState.update {
                it.copy(
                    pageState =
                        it.pageState.copy(
                            page = page,
                        ),
                )
            }
        }
    }

    fun deleteItem(cartId: String) {
        viewModelScope.launch {
            cartRepository.deleteItem(cartId)

            getCartItemsByPage()
        }
    }

    fun updateQuantity(
        productId: String,
        quantity: Int,
    ) {
        viewModelScope.launch {
            cartRepository.setCartItem(productId, quantity = quantity)

            getCartItemsByPage()
        }
    }

    fun checkItem(cartItemId: String) {
        val selectedItems =
            if (_uiState.value.selectedCartState.selectedCartItems
                    .contains(cartItemId)
            ) {
                _uiState.value.selectedCartState.selectedCartItems - cartItemId
            } else {
                _uiState.value.selectedCartState.selectedCartItems + cartItemId
            }.toImmutableList()

        _uiState.update {
            it.copy(
                selectedCartState =
                    it.selectedCartState.copy(
                        selectedCartItems = selectedItems,
                        isAllChecked =
                            _uiState.value.items.all { item ->
                                selectedItems.contains(item.id)
                            },
                    ),
            )
        }

        viewModelScope.launch {
            getCartItemsByPage()
        }
    }

    private fun isSelected(cartItemId: String): Boolean =
        _uiState.value.selectedCartState.selectedCartItems
            .contains(cartItemId)

    fun isAllSelectClick() {
        val selectedItems =
            if (_uiState.value.selectedCartState.isAllChecked) {
                _uiState.value.selectedCartState.selectedCartItems -
                    _uiState.value.items
                        .map { it.id }
                        .toImmutableList()
            } else {
                _uiState.value.selectedCartState.selectedCartItems + _uiState.value.items.map { it.id }
            }.toImmutableList()

        _uiState.update {
            it.copy(
                selectedCartState =
                    it.selectedCartState.copy(
                        selectedCartItems = selectedItems,
                        isAllChecked = !it.selectedCartState.isAllChecked,
                    ),
            )
        }

        viewModelScope.launch {
            getCartItemsByPage()
        }
    }

    suspend fun loadRecommendProducts(): List<Product> {
        val productId = recentItemRepository.getLastViewedItem()

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
            if (_uiState.value.uiInfoState.isOrder) {
                val selectedCartItemIds = _uiState.value.selectedCartState.selectedCartItems

                if (selectedCartItemIds.isEmpty()) {
                    _uiEvent.emit(CartUiEvent.ShowToastMessage("상품을 선택해주세요."))
                    return@launch
                }

                _uiEvent.emit(CartUiEvent.NavToPayment(selectedCartItemIds))
            } else {
                _uiState.update {
                    it.copy(
                        uiInfoState =
                            it.uiInfoState.copy(
                                isOrder = true,
                            ),
                        recommendProducts =
                            loadRecommendProducts()
                                .filter { product ->
                                    cartRepository.getCartItemQuantity(product.id) == null
                                }.map { product ->
                                    product
                                        .toUiModel(
                                            quantity =
                                                cartRepository.getCartItemQuantity(
                                                    product.id,
                                                ),
                                        )
                                }.toImmutableList(),
                    )
                }
            }
        }
    }

    fun onBackClick() {
        viewModelScope.launch {
            _uiEvent.emit(CartUiEvent.NavToBack)
        }
    }

    companion object {
        private const val PAGE_SIZE = 5

        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val app = this[APPLICATION_KEY] as ShoppingApplication
                    CartViewModel(
                        cartRepository = app.appContainer.cartRepository,
                        productRepository = app.appContainer.productRepository,
                        recentItemRepository = app.appContainer.recentItemRepository,
                    )
                }
            }
    }
}
