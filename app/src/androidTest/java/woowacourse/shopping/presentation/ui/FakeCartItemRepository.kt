package woowacourse.shopping.presentation.ui

import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.domain.CartItemRepository
import woowacourse.shopping.domain.CartProduct

class FakeCartItemRepository : CartItemRepository {
    val carts = cartProducts.toMutableList()

    override suspend fun getCartItems(
        page: Int,
        size: Int,
    ): Result<List<CartProduct>> {
        return Result.success(carts)
    }

    override suspend fun postCartItem(cartItemRequest: CartItemRequest): Result<Int> {
        carts.add(
            CartProduct(
                productId = cartItemRequest.productId.toLong(),
                name = "",
                imgUrl = "",
                price = 1000L,
                quantity = 10,
                category = "",
                cartId = 0L,
            ),
        )
        return Result.success(cartItemRequest.productId)
    }

    override suspend fun patchCartItem(
        id: Int,
        quantityRequestDto: QuantityRequest,
    ): Result<Unit> {
        carts.add(
            CartProduct(
                productId = id.toLong(),
                name = "",
                imgUrl = "",
                price = 1000L,
                quantity = 10,
                category = "",
                cartId = 0L,
            ),
        )
        return Result.success(Unit)
    }

    override suspend fun deleteCartItem(id: Int): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getCartItemsCounts(): Result<Int> {
        TODO("Not yet implemented")
    }
}
