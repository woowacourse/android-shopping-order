package woowacourse.shopping

import android.app.Application
import woowacourse.shopping.repository.MemoryProductRepository
import woowacourse.shopping.repository.MemoryQuantityRepository
import woowacourse.shopping.repository.MemoryShoppingCartRepository
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.QuantityRepository
import woowacourse.shopping.repository.ShoppingCartRepository

class ShoppingApplication : Application() {
    companion object {
        val productRepository: ProductRepository = MemoryProductRepository(products = preparedProducts)
        val shoppingCartRepository: ShoppingCartRepository = MemoryShoppingCartRepository()
        val quantityRepository: QuantityRepository = MemoryQuantityRepository(products = preparedProducts)
    }
}
