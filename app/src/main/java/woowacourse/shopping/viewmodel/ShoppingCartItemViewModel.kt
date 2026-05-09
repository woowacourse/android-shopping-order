package woowacourse.shopping.viewmodel

import androidx.lifecycle.ViewModel
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.model.ShoppingCartItem
import woowacourse.shopping.repository.ShoppingItemRepository
import woowacourse.shopping.repository.ShoppingCartRepository

class ShoppingCartItemViewModel(
//    private val shoppingItemRepository: ShoppingItemRepository = ShoppingApplication.shoppingItemRepository,
//    private val shoppingCartRepository: ShoppingCartRepository = ShoppingApplication.shoppingCartRepository,
) : ViewModel() {

//    fun removeShoppingCartItem(shoppingCartItem: ShoppingCartItem) {
//        clearQuantity(shoppingCartItem.product.id)
//        shoppingCartRepository.remove(shoppingCartItem)
//    }
//
//    fun increaseShoppingCartItemQuantity(shoppingCartItem: ShoppingCartItem) {
//        shoppingItemRepository.plusQuantity(shoppingCartItem.product.id)
//    }
//
//    fun decreaseShoppingCartItemQuantity(shoppingCartItem: ShoppingCartItem) {
//        val productId = shoppingCartItem.product.id
//        if (shoppingItemRepository.getQuantity(productId) <= 0) {
//            return
//        }
//        shoppingItemRepository.minusQuantity(productId)
//    }
//
//    private fun clearQuantity(productId: Long) {
//        while (shoppingItemRepository.getQuantity(productId) > 0) {
//            shoppingItemRepository.minusQuantity(productId)
//        }
//    }
}
