package woowacourse.shopping.data.datasource.local.cart

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import woowacourse.shopping.data.local.room.shoppingcart.ShoppingCartDao
import woowacourse.shopping.data.local.room.shoppingcart.ShoppingCartEntity
import woowacourse.shopping.data.local.room.shoppingcart.toDomain
import woowacourse.shopping.domain.model.ShoppingCartItem

class RoomShoppingCartLocalDataSource(
    private val shoppingCartDao: ShoppingCartDao,
) : ShoppingCartLocalDataSource {
    override fun observeShoppingCartItems(): Flow<List<ShoppingCartItem>> =
        shoppingCartDao
            .observeShoppingCartItemRows()
            .map { shoppingCartItemRows -> shoppingCartItemRows.map { shoppingCartItemRow -> shoppingCartItemRow.toDomain() } }

    override suspend fun addIfAbsent(productId: Long) {
        shoppingCartDao.insert(
            ShoppingCartEntity(
                productId = productId,
            ),
        )
    }

    override suspend fun removeByProductId(productId: Long) {
        shoppingCartDao.deleteByProductId(productId)
    }
}
