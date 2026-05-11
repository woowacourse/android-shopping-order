package woowacourse.shopping.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.model.ShoppingCartItem
import woowacourse.shopping.repository.ShoppingCartRepository
import woowacourse.shopping.repository.ShoppingItemRepository
import woowacourse.shopping.ui.pagination.ShoppingCartPageStateHolder

class ShoppingCartItemViewModel(
    private val shoppingCartRepository: ShoppingCartRepository = ShoppingApplication.shoppingCartRepository,
    private val shoppingItemRepository: ShoppingItemRepository = ShoppingApplication.shoppingItemRepository,
) : ViewModel() {
    private val shoppingCartPageStateHolder = ShoppingCartPageStateHolder(shoppingCartItems = emptyList())

    private val _shoppingCartItems: MutableStateFlow<ShoppingCartItemsState> =
        MutableStateFlow(createShoppingCartItemsState(emptyList()))
    val shoppingCartItems: StateFlow<ShoppingCartItemsState> = _shoppingCartItems

    init {
        launchNow {
            val initialItems = shoppingCartRepository.getShoppingItems().toList()
            shoppingCartPageStateHolder.updateItems(initialItems)
            _shoppingCartItems.value = createShoppingCartItemsState(initialItems)
        }
    }

    fun moveToPreviousPage() {
        updatePage(_shoppingCartItems.value.currentPage - 1)
    }

    fun moveToNextPage() {
        updatePage(_shoppingCartItems.value.currentPage + 1)
    }

    fun removeShoppingItem(shoppingCartItem: ShoppingCartItem) {
        launchNow {
            shoppingCartRepository.remove(shoppingCartItem)
            resetQuantity(shoppingCartItem.product.id)
            syncShoppingCartItems()
        }
    }

    fun increaseShoppingItemQuantity(shoppingCartItem: ShoppingCartItem) {
        launchNow {
            shoppingItemRepository.plusQuantity(shoppingCartItem.product.id)
            syncShoppingCartItems()
        }
    }

    fun decreaseShoppingItemQuantity(shoppingCartItem: ShoppingCartItem) {
        launchNow {
            val productId = shoppingCartItem.product.id
            val currentQuantity = shoppingItemRepository.getQuantity(productId)
            if (currentQuantity == 0) {
                return@launchNow
            }
            shoppingItemRepository.minusQuantity(productId)
            if (currentQuantity == 1) {
                shoppingCartRepository.removeByProductId(productId)
            }
            syncShoppingCartItems()
        }
    }

    fun getQuantityPrice(shoppingCartItem: ShoppingCartItem): Int = shoppingCartItem.getProductQuantityPrice()

    private suspend fun syncShoppingCartItems() {
        val latestShoppingCartItems = shoppingCartRepository.getShoppingItems().toList()
        shoppingCartPageStateHolder.updateItems(latestShoppingCartItems)
        _shoppingCartItems.value = createShoppingCartItemsState(latestShoppingCartItems)
    }

    private fun updatePage(page: Int) {
        shoppingCartPageStateHolder.restoreCurrentPage(page)
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

    private fun launchNow(block: suspend () -> Unit) {
        viewModelScope.launch(start = CoroutineStart.UNDISPATCHED) {
            block()
        }
    }

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
}
