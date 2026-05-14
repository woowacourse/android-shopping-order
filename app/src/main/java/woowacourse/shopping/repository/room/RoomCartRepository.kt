package woowacourse.shopping.repository.room

import woowacourse.shopping.local.cart.CartItemDao
import woowacourse.shopping.local.cart.CartItemEntity
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.repository.cart.CartPageItem
import woowacourse.shopping.repository.cart.CartPageResult

class RoomCartRepository(
    private val cartItemDao: CartItemDao,
) : CartRepository {
    override suspend fun createOrder(cartItemIds: List<Long>) {
        cartItemIds.forEach { cartItemId ->
            cartItemDao.deleteBy(cartItemId)
        }
    }

    override suspend fun setQuantity(
        productId: Long,
        quantity: Int,
    ) {
        require(quantity >= 0) { "수량은 0 이상이어야 합니다." }

        val existingItem = cartItemDao.findBy(productId)

        if (quantity == 0) {
            cartItemDao.deleteBy(productId)
            return
        }

        if (existingItem == null) {
            cartItemDao.upsert(
                CartItemEntity(
                    productId = productId,
                    quantity = quantity,
                    createdAtMillis = System.currentTimeMillis(),
                ),
            )
            return
        }

        cartItemDao.upsert(existingItem.copy(quantity = quantity))
    }

    override suspend fun getCartPage(
        page: Int,
        size: Int,
    ): CartPageResult {
        val safePage = page.coerceAtLeast(0)
        val safeSize = size.coerceAtLeast(0)
        val totalElements = cartItemDao.count()
        val fromIndex = safePage * safeSize
        val items =
            cartItemDao
                .getCartItems(fromIndex, safeSize)
                .map { item ->
                    CartPageItem(
                        cartItemId = item.productId,
                        productId = (item.productId),
                        quantity = item.quantity,
                    )
                }
        val totalPages =
            if (safeSize == 0 || totalElements == 0) {
                0
            } else {
                (totalElements - 1) / safeSize + 1
            }

        return CartPageResult(
            items = items,
            totalElements = totalElements,
            totalPages = totalPages,
            page = safePage,
        )
    }

    override suspend fun getCartItemsByProductIds(productIds: Set<Long>): List<CartItem> {
        if (productIds.isEmpty()) return emptyList()

        return cartItemDao
            .getCartItemsByProductIds(productIds.toSet())
            .map(CartItemEntity::toDomain)
    }

    override suspend fun count(): Int = cartItemDao.count()
}
