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
import woowacourse.shopping.ui.model.CartItemUiModel
import woowacourse.shopping.ui.model.mapper.toUiModel

class CartViewModel(
    private val cartRepository: CartRepository,
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
        cartRepository
            .getCartItemsByPage(page = targetPage, size = PAGE_SIZE)
            .onSuccess { cartResponseResult ->
                val items =
                    cartResponseResult.cartItems
                        .map { cartItem ->
                            cartItem.toUiModel(isSelected(cartItem.id))
                        }.toImmutableList()

                val totalCartQuantity =
                    cartRepository
                        .getTotalCartItemQuantity()
                        .getOrDefault(_uiState.value.totalCartQuantity)

                _uiState.update { state ->
                    state.copy(
                        page = targetPage,
                        items = items,
                        isCanMoveNext = cartResponseResult.isLastPage.not(),
                        totalCartQuantity = totalCartQuantity,
                        totalCartCount = cartResponseResult.totalElement,
                        totalPrice = calculateTotalPrice(items, state.selectedCartItems),
                        errorMessage = null,
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(errorMessage = "카트 정보를 불러오는데 실패했습니다.")
                }
            }
    }

    fun nextPage() {
        if (_uiState.value.isCanMoveNext.not()) return
        viewModelScope.launch {
            getCartItemsByPage(targetPage = uiState.value.page + 1)
        }
    }

    fun previousPage() {
        if (_uiState.value.page == 0) return
        viewModelScope.launch {
            getCartItemsByPage(targetPage = uiState.value.page - 1)
        }
    }

    fun deleteItem(cartId: Long) {
        viewModelScope.launch {
            cartRepository
                .deleteItem(cartId)
                .onSuccess {
                    _uiState.update { state ->
                        val selectedItem = state.selectedCartItems - cartId

                        state.copy(
                            selectedCartItems = selectedItem.toImmutableList(),
                            selectedCartItemCount = selectedItem.size,
                            errorMessage = null,
                        )
                    }
                    getCartItemsByPage()
                }.onFailure {
                    _uiState.update { it.copy(errorMessage = "상품 삭제에 실패했습니다.") }
                }
        }
    }

    fun updateQuantity(
        productId: Long,
        quantity: Int,
    ) {
        viewModelScope.launch {
            cartRepository
                .setCartItem(productId, quantity = quantity)
                .onSuccess {
                    getCartItemsByPage()
                }.onFailure {
                    _uiState.update { it.copy(errorMessage = "수량 변경에 실패했습니다.") }
                }
        }
    }

    fun checkItem(cartItemId: Long) {
        _uiState.update { state ->
            val selectedItems =
                if (state.selectedCartItems.contains(cartItemId)) {
                    state.selectedCartItems - cartItemId
                } else {
                    state.selectedCartItems + cartItemId
                }.toImmutableList()
            val items =
                state.items
                    .map { item ->
                        if (item.id == cartItemId) {
                            item.copy(isChecked = item.isChecked.not())
                        } else {
                            item
                        }
                    }.toImmutableList()

            state.copy(
                selectedCartItems = selectedItems,
                selectedCartItemCount = selectedItems.size,
                totalPrice = calculateTotalPrice(items, selectedItems),
                items = items,
                isAllChecked = items.isNotEmpty() && selectedItems.size == items.size,
            )
        }
    }

    private fun isSelected(cartItemId: Long): Boolean = _uiState.value.selectedCartItems.contains(cartItemId)

    fun isAllSelectClick() {
        _uiState.update { state ->
            val changedAllChecked = state.isAllChecked.not()

            val selectedItems =
                if (changedAllChecked) {
                    state.items.map { it.id }
                } else {
                    emptyList()
                }.toImmutableList()

            val items =
                state.items
                    .map { item ->
                        item.copy(isChecked = changedAllChecked)
                    }.toImmutableList()

            state.copy(
                items = items,
                selectedCartItems = selectedItems,
                selectedCartItemCount = selectedItems.size,
                isAllChecked = !state.isAllChecked,
                totalPrice = calculateTotalPrice(items, selectedItems),
            )
        }
    }

    private fun calculateTotalPrice(
        items: List<CartItemUiModel>,
        selectedCartItemIds: List<Long>,
    ): Long = items.filter { it.id in selectedCartItemIds }.sumOf { it.totalPrice }

    companion object {
        private const val PAGE_SIZE = 5

        fun provideFactory(cartRepository: CartRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    CartViewModel(
                        cartRepository = cartRepository,
                    )
                }
            }
    }
}
