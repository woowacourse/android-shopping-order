package woowacourse.shopping.repository

import woowacourse.shopping.model.ShoppingCartItem
import woowacourse.shopping.model.ShoppingItem
import woowacourse.shopping.storage.room.shoppingcart.ShoppingCartDao
import woowacourse.shopping.storage.room.shoppingcart.ShoppingCartEntity
import woowacourse.shopping.storage.room.shoppingcart.toDomain

class RoomShoppingCartRepository(
    private val shoppingCartDao: ShoppingCartDao,
) : ShoppingCartRepository {
    override suspend fun add(shoppingItem: ShoppingItem) {
        shoppingCartDao.insert(
            ShoppingCartEntity(
                productId = shoppingItem.getProductId(),
            ),
        )
    }

    override suspend fun remove(shoppingCartItem: ShoppingCartItem) {
        val removedCount = shoppingCartDao.deleteById(shoppingCartItem.getId())
        if (removedCount == 0) {
            throw IllegalArgumentException("장바구니에 존재하지 않는 상품입니다")
        }
    }

    override suspend fun getShoppingItems(): List<ShoppingCartItem> =
        shoppingCartDao.getShoppingCartItemRows().map { shoppingCartItemRow -> shoppingCartItemRow.toDomain() }

    override suspend fun containsProduct(productId: Long): Boolean = shoppingCartDao.existsByProductId(productId)

    override suspend fun removeByProductId(productId: Long): Boolean = shoppingCartDao.deleteByProductId(productId) > 0

    override suspend fun getTotalQuantity(): Int = shoppingCartDao.getTotalQuantity()
}
