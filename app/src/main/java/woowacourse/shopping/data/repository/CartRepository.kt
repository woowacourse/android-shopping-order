package woowacourse.shopping.data.repository

import woowacourse.shopping.data.api.CartApi
import woowacourse.shopping.data.model.request.CartItemQuantityRequest
import woowacourse.shopping.data.model.request.CartItemRequest
import woowacourse.shopping.data.model.response.CartItemsResponse.Content.Companion.toDomain
import woowacourse.shopping.domain.model.Page
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.CartRepository

class CartRepository(
    private val api: CartApi,
) : CartRepository {
    override suspend fun fetchCartProducts(
        page: Int,
        size: Int,
    ): Products {
        val response = api.getCartItems(page, size)
        val items = response.content.map { it.toDomain() }
        val pageInfo = Page(page, response.first, response.last)
        return Products(items, pageInfo)
    }

    override suspend fun fetchAllCartProducts(): Products {
        val firstPage = 0
        val maxSize = Int.MAX_VALUE

        return fetchCartProducts(firstPage, maxSize)
    }

    override suspend fun fetchCartItemCount(): Int {
        val response = api.getCartItemsCount()
        return response.quantity
    }

    override suspend fun addCartProduct(
        productId: Long,
        quantity: Int,
    ) {
        api.postCartItem(CartItemRequest(productId = productId, quantity = quantity))
    }

    override suspend fun updateCartProduct(
        cartId: Long,
        quantity: Int,
    ) {
        api.patchCartItem(cartId, CartItemQuantityRequest(quantity))
    }

    override suspend fun deleteCartProduct(cartId: Long) {
        api.deleteCartItem(cartId)
    }
}
