package woowacourse.shopping.remote.datasource

import retrofit2.Response
import woowacourse.shopping.data.datasource.remote.ShoppingRemoteCartDataSource
import woowacourse.shopping.domain.model.Carts
import woowacourse.shopping.remote.api.CartService
import woowacourse.shopping.remote.mapper.toDomain
import woowacourse.shopping.remote.model.request.PatchCartItemRequest
import woowacourse.shopping.remote.model.request.PostCartItemRequest

class ShoppingRemoteCartDataSourceImpl(private val service: CartService) :
    ShoppingRemoteCartDataSource {
    override suspend fun insertCartProduct(
        productId: Long,
        quantity: Int,
    ): Int {
        val body =
            PostCartItemRequest(
                productId = productId.toInt(),
                quantity = quantity,
            )

        val response = service.postCartItem(body).headers()["location"]

        return response?.split("/")?.last()?.toInt() ?: throw IllegalArgumentException()
    }

    override suspend fun updateCartProduct(
        cartId: Int,
        quantity: Int,
    ) {
        val body = PatchCartItemRequest(quantity = quantity)
        service.patchCartItem(id = cartId, body = body)
    }

    override suspend fun getCartProductsPaged(
        page: Int,
        size: Int,
    ): Carts = service.getCartItems(page = page, size = size).toDomain()

    override suspend fun getCartProductsQuantity(): Int = service.getCartItemsCount().quantity

    override suspend fun deleteCartProductById(cartId: Int) {
        service.deleteCartItem(id = cartId)
    }

    fun <T> Response<T>.toCartId(): Int = this.headers()["location"]?.split("/")?.last()?.toInt() ?: throw IllegalArgumentException()
}
