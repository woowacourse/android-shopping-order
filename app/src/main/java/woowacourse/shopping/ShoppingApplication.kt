package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.repository.MemoryShoppingCartRepository
import woowacourse.shopping.repository.MemoryShoppingItemRepository
import woowacourse.shopping.repository.ShoppingCartRepository
import woowacourse.shopping.repository.ShoppingItemRepository

class ShoppingApplication : Application() {
    companion object {
        val shoppingItemRepository: ShoppingItemRepository =
            MemoryShoppingItemRepository(shoppingItems = preparedProducts)
        val shoppingCartRepository: ShoppingCartRepository =
            MemoryShoppingCartRepository()
    }
}
