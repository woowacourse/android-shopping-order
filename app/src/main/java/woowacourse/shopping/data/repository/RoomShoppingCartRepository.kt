package woowacourse.shopping.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import woowacourse.shopping.domain.model.ShoppingCartItem
import woowacourse.shopping.data.local.room.shoppingcart.ShoppingCartDao
import woowacourse.shopping.data.local.room.shoppingcart.ShoppingCartEntity
import woowacourse.shopping.data.local.room.shoppingcart.toDomain
import woowacourse.shopping.domain.repository.ShoppingCartRepository

class RoomShoppingCartRepository(
    private val shoppingCartDao: ShoppingCartDao,
) : ShoppingCartRepository {
    override fun observeShoppingItems(): Flow<List<ShoppingCartItem>> =
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

    override suspend fun removeByProductId(productId: Long): Boolean = shoppingCartDao.deleteByProductId(productId) > 0
}
