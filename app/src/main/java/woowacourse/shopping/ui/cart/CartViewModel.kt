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

                _uiState.update { state ->
                    val selectedCartItems =
                        state.selectedCartItems +
                            items
                                .filter { state.selectedCartItems.containsKey(it.id) }
                                .associate { it.id to it.toSelectedCartItem() }
                    state.copy(
                        page = targetPage,
                        items = items,
                        isCanMoveNext = cartResponseResult.isLastPage.not(),
                        totalCartQuantity = calculateTotalQuantity(selectedCartItems),
                        totalCartCount = cartResponseResult.totalElement,
                        selectedCartItems = selectedCartItems,
                        totalPrice = calculateTotalPrice(selectedCartItems),
                        isAllChecked =
                            selectedCartItems.size.toLong() == cartResponseResult.totalElement && cartResponseResult.totalElement > 0,
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
                    val targetPage = calculatePage()
                    _uiState.update { state ->
                        val selectedItem = state.selectedCartItems - cartId

                        state.copy(
                            selectedCartItems = selectedItem,
                            totalCartQuantity = calculateTotalQuantity(selectedItem),
                            totalPrice = calculateTotalPrice(selectedItem),
                            isAllChecked = selectedItem.size.toLong() == state.totalCartCount - 1 && state.totalCartCount - 1 > 0,
                            errorMessage = null,
                        )
                    }
                    getCartItemsByPage(targetPage)
                }.onFailure {
                    _uiState.update { it.copy(errorMessage = "상품 삭제에 실패했습니다.") }
                }
        }
    }

    private fun calculatePage(): Int {
        val nextTotalCount = (_uiState.value.totalCartCount - 1).coerceAtLeast(0)
        val lastPage = ((nextTotalCount - 1) / PAGE_SIZE).coerceAtLeast(0)

        return minOf(_uiState.value.page, lastPage.toInt())
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
            val item = state.items.firstOrNull { it.id == cartItemId } ?: return@update state
            val selectedItems =
                if (state.selectedCartItems.containsKey(cartItemId)) {
                    state.selectedCartItems - cartItemId
                } else {
                    state.selectedCartItems + (cartItemId to item.toSelectedCartItem())
                }
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
                items = items,
                selectedCartItems = selectedItems,
                totalCartQuantity = calculateTotalQuantity(selectedItems),
                totalPrice = calculateTotalPrice(selectedItems),
                isAllChecked = selectedItems.size.toLong() == state.totalCartCount && state.totalCartCount > 0,
            )
        }
    }

    private fun isSelected(cartItemId: Long): Boolean = _uiState.value.selectedCartItems.containsKey(cartItemId)

    fun isAllSelectClick() {
        viewModelScope.launch {
            if (_uiState.value.isAllChecked) {
                _uiState.update { state ->
                    val items =
                        state.items
                            .map { it.copy(isChecked = false) }
                            .toImmutableList()
                    state.copy(
                        selectedCartItems = emptyMap(),
                        totalCartQuantity = 0,
                        totalPrice = 0,
                        isAllChecked = false,
                        items = items,
                        errorMessage = null,
                    )
                }
                return@launch
            }

            cartRepository
                .getAllCartItems()
                .onSuccess { cartItems ->
                    val selectedCartItem =
                        cartItems.associate { cartItem ->
                            cartItem.id to
                                SelectedCartItem(
                                    totalPrice = cartItem.getTotalPrice().amount,
                                    quantity = cartItem.quantity,
                                )
                        }

                    _uiState.update { state ->
                        val items = state.items.map { it.copy(isChecked = true) }.toImmutableList()
                        state.copy(
                            items = items,
                            selectedCartItems = selectedCartItem,
                            totalCartQuantity = calculateTotalQuantity(selectedCartItem),
                            totalPrice = calculateTotalPrice(selectedCartItem),
                            isAllChecked = true,
                            errorMessage = null,
                        )
                    }
                }.onFailure {
                    _uiState.update {
                        it.copy(errorMessage = "전체 선택에 실패했습니다.")
                    }
                }
        }
    }

    private fun CartItemUiModel.toSelectedCartItem(): SelectedCartItem =
        SelectedCartItem(
            totalPrice = totalPrice,
            quantity = quantity,
        )

    private fun calculateTotalPrice(selectedCartItems: Map<Long, SelectedCartItem>): Long = selectedCartItems.values.sumOf { it.totalPrice }

    private fun calculateTotalQuantity(selectedCartItems: Map<Long, SelectedCartItem>): Int = selectedCartItems.values.sumOf { it.quantity }

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
