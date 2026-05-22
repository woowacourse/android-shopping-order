package woowacourse.shopping.repository

import woowacourse.shopping.model.cart.Cart
import woowacourse.shopping.model.cart.CartItem
import woowacourse.shopping.repository.query.CartPageResult

class FakeCartRepository : CartRepository {
    private var cart = Cart(emptyList())

    override suspend fun createOrder(cartItemIds: List<Long>): Result<Unit> {
        cartItemIds.forEach { cartItemId ->
            cart = cart.setQuantity(cartItemId, 0)
        }
        return Result.success(Unit)
    }

    override suspend fun setQuantity(
        productId: Long,
        quantity: Int,
    ): Result<Unit> {
        if (quantity < 0) {
            return Result.failure(
                IllegalArgumentException("수량은 0 이상이어야 합니다."),
            )
        }

        cart = cart.setQuantity(productId, quantity)
        return Result.success(Unit)
    }

    override suspend fun getCartPage(
        page: Int,
        size: Int,
    ): Result<CartPageResult> {
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

        return Result.success(
            CartPageResult(
                items = items,
                totalElements = totalElements,
                totalPages = totalPages,
                page = safePage,
            ),
        )
    }

    override suspend fun getCartItemsByProductIds(productIds: Set<Long>): Result<List<CartItem>> =
        Result.success(
            cart.items.filter { it.productId in productIds },
        )

    override suspend fun count(): Result<Int> = Result.success(cart.count())
}
