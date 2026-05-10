package woowacourse.shopping

import android.app.Application
import kotlinx.coroutines.runBlocking
import woowacourse.shopping.repository.RoomShoppingCartRepository
import woowacourse.shopping.repository.RoomShoppingItemRepository
import woowacourse.shopping.repository.ShoppingCartRepository
import woowacourse.shopping.repository.ShoppingItemRepository
import woowacourse.shopping.storage.room.ShoppingDatabase
import woowacourse.shopping.storage.room.toEntity

class ShoppingApplication : Application() {
    companion object {
        lateinit var shoppingItemRepository: ShoppingItemRepository
            private set
        lateinit var shoppingCartRepository: ShoppingCartRepository
            private set
    }

    override fun onCreate() {
        super.onCreate()

        val shoppingDatabase = ShoppingDatabase.create(this)
        seedShoppingItemsIfNeeded(shoppingDatabase)

        shoppingItemRepository = RoomShoppingItemRepository(shoppingDatabase.shoppingItemDao())
        shoppingCartRepository = RoomShoppingCartRepository(shoppingDatabase.shoppingCartDao())
    }

    private fun seedShoppingItemsIfNeeded(shoppingDatabase: ShoppingDatabase) {
        runBlocking {
            val shoppingItemDao = shoppingDatabase.shoppingItemDao()
            if (shoppingItemDao.count() > 0) {
                return@runBlocking
            }
            shoppingItemDao.insertAll(preparedProducts.map { shoppingItem -> shoppingItem.toEntity() })
        }
    }
}
