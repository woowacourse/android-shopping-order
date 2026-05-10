package woowacourse.shopping.repository

import kotlinx.coroutines.runBlocking
import woowacourse.shopping.model.ShoppingCartItem
import woowacourse.shopping.model.ShoppingItem
import woowacourse.shopping.storage.room.shoppingcart.ShoppingCartDao
import woowacourse.shopping.storage.room.shoppingcart.ShoppingCartEntity
import woowacourse.shopping.storage.room.toDomain

class RoomShoppingCartRepository(
    private val shoppingCartDao: ShoppingCartDao,
) : ShoppingCartRepository {
    override fun add(shoppingItem: ShoppingItem) {
        runBlocking {
            shoppingCartDao.insert(
                ShoppingCartEntity(
                    productId = shoppingItem.getProductId(),
                ),
            )
        }
    }

    override fun remove(shoppingCartItem: ShoppingCartItem) {
        runBlocking {
            val targetRow =
                shoppingCartDao
                    .getShoppingCartItemRows()
                    .firstOrNull { shoppingCartItemRow -> shoppingCartItemRow.toDomain() == shoppingCartItem }
                    ?: throw IllegalArgumentException("장바구니에 존재하지 않는 상품입니다")
            val removedCount = shoppingCartDao.deleteById(targetRow.id)
            if (removedCount == 0) {
                throw IllegalArgumentException("장바구니에 존재하지 않는 상품입니다")
            }
        }
    }

    override fun getShoppingItems(): List<ShoppingCartItem> =
        runBlocking {
            shoppingCartDao.getShoppingCartItemRows().map { shoppingCartItemRow -> shoppingCartItemRow.toDomain() }
        }
}

