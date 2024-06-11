package woowacourse.shopping.remote.datasource

import woowacourse.shopping.data.datasource.remote.ShoppingCartRemoteDataSource
import woowacourse.shopping.domain.model.Carts
import woowacourse.shopping.remote.api.CartService
import woowacourse.shopping.remote.mapper.toDomain
import woowacourse.shopping.remote.model.request.PatchCartItemRequest
import woowacourse.shopping.remote.model.request.PostCartItemRequest

class ShoppingCartRemoteDataSourceImpl(private val service: CartService) :
    ShoppingCartRemoteDataSource {
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

    override suspend fun getCartsTotalElement(): Int = service.getCartItems().totalElements

    override suspend fun getEntireCarts(size: Int): Carts = service.getCartItems(size = size).toDomain()

    override suspend fun getCartProductsQuantity(): Int = service.getCartItemsCount().quantity

    override suspend fun deleteCartProductById(cartId: Int): Unit = service.deleteCartItem(id = cartId)
}
