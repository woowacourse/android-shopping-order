package woowacourse.shopping.repository

import woowacourse.shopping.cartProducts
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.repository.CartItemRepository

class FakeCartItemRepository : CartItemRepository {
    val carts = cartProducts.toMutableList()

    override suspend fun getAllByPaging(
        page: Int,
        size: Int,
    ): Result<List<CartProduct>> {
        return Result.success(carts)
    }

    override suspend fun post(cartItemRequest: CartItemRequest): Result<Int> {
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

    override suspend fun patch(
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

    override suspend fun delete(id: Int): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getCount(): Result<Int> {
        TODO("Not yet implemented")
    }
}
