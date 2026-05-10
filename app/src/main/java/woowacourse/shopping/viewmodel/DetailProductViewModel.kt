package woowacourse.shopping.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.model.ShoppingItem
import woowacourse.shopping.repository.ShoppingCartRepository
import woowacourse.shopping.repository.ShoppingItemRepository

class DetailProductViewModel(
    private val shoppingCartRepository: ShoppingCartRepository = ShoppingApplication.shoppingCartRepository,
    private val shoppingItemRepository: ShoppingItemRepository = ShoppingApplication.shoppingItemRepository,
) : ViewModel() {
    val shoppingItems: StateFlow<List<ShoppingItem>> = shoppingItemRepository.shoppingItems

    fun defaultQuantity(): Int = 1

    fun addProductToCart(
        shoppingItem: ShoppingItem,
        selectedQuantity: Int,
    ) {
        if (selectedQuantity < 1) {
            return
        }
        val productId = shoppingItem.getProductId()
        val sourceItem = shoppingItemRepository.getShoppingItemOrNull(productId) ?: return
        shoppingCartRepository.add(sourceItem)
        shoppingItemRepository.plusQuantity(productId, selectedQuantity)
    }

    fun quantityPrice(
        shoppingItem: ShoppingItem,
        selectedQuantity: Int,
    ): Int = shoppingItem.getProductQuantityPrice(selectedQuantity)
}
