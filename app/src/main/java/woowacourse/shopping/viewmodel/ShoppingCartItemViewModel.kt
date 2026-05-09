package woowacourse.shopping.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.model.ShoppingCartItem
import woowacourse.shopping.model.ShoppingItem
import woowacourse.shopping.repository.ShoppingCartRepository
import woowacourse.shopping.repository.ShoppingItemRepository

class ShoppingCartItemViewModel(
    private val shoppingCartRepository: ShoppingCartRepository = ShoppingApplication.shoppingCartRepository,
    private val shoppingItemRepository: ShoppingItemRepository = ShoppingApplication.shoppingItemRepository,
) : ViewModel() {
    private val _shoppingCartItems: MutableStateFlow<List<ShoppingCartItem>> =
        MutableStateFlow(shoppingCartRepository.getShoppingItems().toList())
    val shoppingCartItems: StateFlow<List<ShoppingCartItem>> = _shoppingCartItems

    val shoppingItems: StateFlow<List<ShoppingItem>> = shoppingItemRepository.shoppingItems

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

    private fun syncShoppingCartItems() {
        _shoppingCartItems.value = shoppingCartRepository.getShoppingItems().toList()
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
        while (shoppingItemRepository.getQuantity(productId) > 0) {
            shoppingItemRepository.minusQuantity(productId)
        }
    }

}
