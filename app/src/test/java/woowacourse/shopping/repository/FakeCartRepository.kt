package woowacourse.shopping.repository

import woowacourse.shopping.model.Cart
import woowacourse.shopping.model.CartItem

class FakeCartRepository : CartRepository {
    private var cart = Cart(emptyList())

    override suspend fun createOrder(cartItemIds: List<Long>) {
        cartItemIds.forEach { cartItemId ->
            cart = cart.setQuantity(cartItemId, 0)
        }
    }

    override suspend fun setQuantity(
        productId: Long,
        quantity: Int,
    ) {
        cart = cart.setQuantity(productId, quantity)
    }

    override suspend fun getCartPage(
        page: Int,
        size: Int,
    ): woowacourse.shopping.repository.query.CartPageResult {
        val safePage = page.coerceAtLeast(0)
        val safeSize = size.coerceAtLeast(0)
        val totalElements = cart.items.size
        val fromIndex = safePage * safeSize
        val safeFrom = fromIndex.coerceIn(0, totalElements)
        val safeTo = minOf(safeFrom + safeSize, totalElements)
        val items =
            cart.items.subList(safeFrom, safeTo).map {
                woowacourse.shopping.repository.query.CartPageItem(
                    cartItemId = it.productId,
                    productId = it.productId,
                    quantity = it.quantity,
                )
            }
        val totalPages =
            if (safeSize == 0 || totalElements == 0) {
                0
            } else {
                (totalElements - 1) / safeSize + 1
            }

        return woowacourse.shopping.repository.query.CartPageResult(
            items = items,
            totalElements = totalElements,
            totalPages = totalPages,
            page = safePage,
        )
    }

    override suspend fun getCartItemsByProductIds(productIds: Set<Long>): List<CartItem> = cart.items.filter { it.productId in productIds }

    override suspend fun count(): Int = cart.count()
}
