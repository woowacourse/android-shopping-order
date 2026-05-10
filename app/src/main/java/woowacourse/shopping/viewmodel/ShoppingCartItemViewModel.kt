package woowacourse.shopping.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.model.ShoppingCartItem
import woowacourse.shopping.repository.ShoppingCartRepository
import woowacourse.shopping.repository.ShoppingItemRepository

class ShoppingCartItemViewModel(
    private val shoppingCartRepository: ShoppingCartRepository = ShoppingApplication.shoppingCartRepository,
    private val shoppingItemRepository: ShoppingItemRepository = ShoppingApplication.shoppingItemRepository,
) : ViewModel() {
    private val _shoppingCartItemsState: MutableStateFlow<ShoppingCartItemsState> =
        MutableStateFlow(
            ShoppingCartItemsState(
                revision = 0L,
                items = shoppingCartRepository.getShoppingItems().toList(),
            ),
        )
    val shoppingCartItems: StateFlow<ShoppingCartItemsState> = _shoppingCartItemsState

    fun removeShoppingItem(shoppingCartItem: ShoppingCartItem) {
        shoppingCartRepository.remove(shoppingCartItem)
        resetQuantity(shoppingCartItem.product.id)
        syncShoppingCartItems()
    }

    fun increaseShoppingItemQuantity(shoppingCartItem: ShoppingCartItem) {
        shoppingItemRepository.plusQuantity(shoppingCartItem.product.id)
        syncShoppingCartItems()
    }

    fun decreaseShoppingItemQuantity(shoppingCartItem: ShoppingCartItem) {
        val productId = shoppingCartItem.product.id
        if (shoppingItemRepository.getQuantity(productId) == 0) {
            return
        }
        shoppingItemRepository.minusQuantity(productId)
        if (shoppingItemRepository.getQuantity(productId) == 0) {
            removeShoppingCartItemByProductId(productId)
        }
        syncShoppingCartItems()
    }

    fun getQuantityPrice(shoppingCartItem: ShoppingCartItem): Int =
        shoppingCartItem.getProductQuantityPrice()

    private fun syncShoppingCartItems() {
        val latestShoppingCartItems = shoppingCartRepository.getShoppingItems().toList()
        val currentRevision = _shoppingCartItemsState.value.revision
        _shoppingCartItemsState.value =
            ShoppingCartItemsState(
                revision = currentRevision + 1,
                items = latestShoppingCartItems,
            )
    }

    private fun removeShoppingCartItemByProductId(productId: Long) {
        val targetItem =
            shoppingCartRepository
                .getShoppingItems()
                .find { shoppingCartItem -> shoppingCartItem.product.id == productId }
                ?: return
        shoppingCartRepository.remove(targetItem)
    }

    private fun resetQuantity(productId: Long) {
        val currentQuantity = shoppingItemRepository.getQuantity(productId)
        if (currentQuantity == 0) {
            return
        }
        shoppingItemRepository.minusQuantity(productId, currentQuantity)
    }

    data class ShoppingCartItemsState(
        val revision: Long,
        val items: List<ShoppingCartItem>,
    )
}
