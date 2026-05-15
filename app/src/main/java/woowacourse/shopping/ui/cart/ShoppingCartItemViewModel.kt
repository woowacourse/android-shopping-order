package woowacourse.shopping.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.ShoppingCartItem
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.domain.repository.ShoppingItemRepository
import woowacourse.shopping.ui.cart.ShoppingCartPageStateHolder

class ShoppingCartItemViewModel(
    private val shoppingCartRepository: ShoppingCartRepository,
    private val shoppingItemRepository: ShoppingItemRepository,
) : ViewModel() {
    private val shoppingCartPageStateHolder = ShoppingCartPageStateHolder(shoppingCartItems = emptyList())
    private val _event = MutableSharedFlow<ShoppingCartEvent>(extraBufferCapacity = 1)
    val event: SharedFlow<ShoppingCartEvent> = _event.asSharedFlow()

    private val _shoppingCartItems: MutableStateFlow<ShoppingCartItemsState> =
        MutableStateFlow(createShoppingCartItemsState(emptyList()))
    val shoppingCartItems: StateFlow<ShoppingCartItemsState> = _shoppingCartItems.asStateFlow()

    init {
        viewModelScope.launch {
            shoppingCartRepository.observeShoppingItems().collect { latestShoppingCartItems ->
                shoppingCartPageStateHolder.updateItems(latestShoppingCartItems)
                _shoppingCartItems.value = createShoppingCartItemsState(latestShoppingCartItems)
            }
        }
    }

    fun moveToPreviousPage() {
        shoppingCartPageStateHolder.beforePage()
        publishCurrentPageState()
    }

    fun moveToNextPage() {
        shoppingCartPageStateHolder.nextPage()
        publishCurrentPageState()
    }

    fun onBackClick() {
        _event.tryEmit(ShoppingCartEvent.NavigateBack)
    }

    fun removeShoppingItem(shoppingCartItem: ShoppingCartItem) {
        viewModelScope.launch {
            shoppingCartRepository.remove(shoppingCartItem)
            resetQuantity(shoppingCartItem.product.id)
        }
    }

    fun getQuantityPrice(shoppingCartItem: ShoppingCartItem): Int = shoppingCartItem.getProductQuantityPrice()

    fun refresh() {
        publishCurrentPageState()
    }

    private fun publishCurrentPageState() {
        val currentItems = _shoppingCartItems.value.items
        _shoppingCartItems.value = createShoppingCartItemsState(currentItems)
    }

    private suspend fun resetQuantity(productId: Long) {
        val currentQuantity = shoppingItemRepository.getQuantity(productId)
        if (currentQuantity == 0) {
            return
        }
        shoppingItemRepository.minusQuantity(productId, currentQuantity)
    }

    private fun createShoppingCartItemsState(items: List<ShoppingCartItem>): ShoppingCartItemsState =
        ShoppingCartItemsState(
            items = items,
            pagedItems = shoppingCartPageStateHolder.getItems(),
            currentPage = shoppingCartPageStateHolder.currentPage,
            canMoveToPreviousPage = shoppingCartPageStateHolder.canMoveToPreviousPage(),
            canMoveToNextPage = shoppingCartPageStateHolder.canMoveToNextPage(),
        )

    data class ShoppingCartItemsState(
        val items: List<ShoppingCartItem>,
        val pagedItems: List<ShoppingCartItem> = emptyList(),
        val currentPage: Int = INITIAL_PAGE,
        val canMoveToPreviousPage: Boolean = false,
        val canMoveToNextPage: Boolean = false,
    )

    companion object {
        private const val INITIAL_PAGE = 0
    }

    sealed interface ShoppingCartEvent {
        data object NavigateBack : ShoppingCartEvent
    }
}
