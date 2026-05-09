package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.model.ShoppingItem
import woowacourse.shopping.repository.MemoryProductRepository
import woowacourse.shopping.repository.MemoryShoppingItemRepository
import woowacourse.shopping.repository.MemoryShoppingCartRepository
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.ShoppingItemRepository
import woowacourse.shopping.repository.ShoppingCartRepository

class ShoppingApplication : Application() {
    companion object {
        val productRepository: ProductRepository = MemoryProductRepository(products = preparedProducts)
        val shoppingCartRepository: ShoppingCartRepository = MemoryShoppingCartRepository()
        val shoppingItemRepository: ShoppingItemRepository =
            MemoryShoppingItemRepository(
                shoppingItems =
                    preparedProducts.map { product ->
                        ShoppingItem(product = product, quantity = 0)
                    },
            )
    }
}
