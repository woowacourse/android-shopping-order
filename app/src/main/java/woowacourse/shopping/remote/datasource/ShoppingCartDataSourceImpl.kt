package woowacourse.shopping.remote.datasource

import retrofit2.Response
import woowacourse.shopping.data.datasource.remote.ShoppingCartDataSource
import woowacourse.shopping.data.model.remote.CartsDto
import woowacourse.shopping.remote.api.CartService
import woowacourse.shopping.remote.mapper.toData
import woowacourse.shopping.remote.model.request.PatchCartItemRequest
import woowacourse.shopping.remote.model.request.PostCartItemRequest

class ShoppingCartDataSourceImpl(private val service: CartService) : ShoppingCartDataSource {
    override fun insertCartProduct(
        productId: Long,
        quantity: Int,
    ): Result<Int> =
        runCatching {
            val body =
                PostCartItemRequest(
                    productId = productId.toInt(),
                    quantity = quantity,
                )
            service.postCartItem(body).execute().toCartId()
        }

    override fun updateCartProduct(
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

    override fun getCartProductsTotal(): Result<Int> =
        runCatching {
            service.getCartItemsCount().execute().body()?.quantity ?: 0
        }

    override fun deleteCartProduct(cartId: Int): Result<Unit> =
        runCatching {
            service.deleteCartItem(id = cartId).execute().body()
        }

    private fun <T> Response<T>.toCartId(): Int =
        this.headers()["location"]?.split("/")?.last()?.toInt() ?: throw IllegalArgumentException()

    companion object {
        private var instance: ShoppingCartDataSourceImpl? = null

        fun setInstance(cartService: CartService) {
            instance = ShoppingCartDataSourceImpl(service = cartService)
        }

        fun getInstance(): ShoppingCartDataSourceImpl = requireNotNull(instance)
    }
}
