package woowacourse.shopping.remote.datasource

import woowacourse.shopping.data.datasource.remote.ShoppingCartDataSource
import woowacourse.shopping.data.model.remote.CartItemIdDto
import woowacourse.shopping.data.model.remote.CartsDto
import woowacourse.shopping.remote.api.CartService
import woowacourse.shopping.remote.mapper.toCartItemIdDto
import woowacourse.shopping.remote.mapper.toData
import woowacourse.shopping.remote.model.request.PatchCartItemRequest
import woowacourse.shopping.remote.model.request.PostCartItemRequest

class ShoppingCartDataSourceImpl(private val service: CartService) : ShoppingCartDataSource {
    override fun postCartItem(
        productId: Long,
        quantity: Int,
    ): Result<CartItemIdDto> =
        runCatching {
            val body =
                PostCartItemRequest(
                    productId = productId.toInt(),
                    quantity = quantity,
                )
            service.postCartItem(body).execute().toCartItemIdDto()
        }

    override fun patchCartItem(
        cartId: Int,
        quantity: Int,
    ): Result<Unit> =
        runCatching {
            val body = PatchCartItemRequest(quantity = quantity)
            service.patchCartItem(id = cartId, body = body).execute()
        }

    override fun getCartProductsPaged(
        page: Int,
        size: Int,
    ): Result<CartsDto> =
        runCatching {
            service.getCartItems(page = page, size = size).execute().body()?.toData()
                ?: throw IllegalArgumentException()
        }

    override fun getCartProductTotalElements(): Result<Int> =
        runCatching {
            service.getCartItems(page = FIRST_PAGE, size = FIRST_SIZE).execute().body()
                ?.toData()?.totalElements ?: 0
        }

    override fun getCartItemsCount(): Result<Int> =
        runCatching {
            service.getCartItemsCount().execute().body()?.quantity ?: 0
        }

    override fun deleteCartItem(cartId: Int): Result<Unit> =
        runCatching {
            service.deleteCartItem(id = cartId).execute().body()
        }

    companion object {
        const val FIRST_PAGE = 0
        const val FIRST_SIZE = 1
    }
}
