package woowacourse.shopping.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.model.ShoppingItem
import woowacourse.shopping.repository.ShoppingItemRepository
import woowacourse.shopping.repository.ShoppingCartRepository

class ProductListViewModel(
    private val shoppingCartRepository: ShoppingCartRepository = ShoppingApplication.shoppingCartRepository,
    private val shoppingItemRepository: ShoppingItemRepository = ShoppingApplication.shoppingItemRepository,
) : ViewModel() {

    val shoppingItems: StateFlow<List<ShoppingItem>> = shoppingItemRepository.shoppingItems

    fun addProductToCart(shoppingItem: ShoppingItem) {
        shoppingCartRepository.add(shoppingItem.getProduct())
        shoppingItemRepository.plusQuantity(shoppingItem.getProductId())
    }

    fun increaseProductQuantity(shoppingItem: ShoppingItem) {
        shoppingItemRepository.plusQuantity(shoppingItem.getProductId())
    }

    fun decreaseProductQuantity(shoppingItem: ShoppingItem) {
        if (shoppingItem.getQuantity() == 0) {
            return
        }
        shoppingItemRepository.minusQuantity(shoppingItem.getProductId())
    }
}
