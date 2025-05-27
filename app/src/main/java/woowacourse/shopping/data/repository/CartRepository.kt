package woowacourse.shopping.data.repository

import woowacourse.shopping.data.api.CartApi
import woowacourse.shopping.data.dao.CartDao
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.model.request.CartItemRequest
import woowacourse.shopping.data.model.response.CartItemsResponse
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.CartProducts
import woowacourse.shopping.domain.model.Page
import woowacourse.shopping.domain.repository.CartRepository

class CartRepository(
    private val dao: CartDao,
    private val api: CartApi,
) : CartRepository {
    override fun fetchCartProductDetail(productId: Int): CartProduct? = dao.getCartProductDetailById(productId)?.toDomain()

    override fun fetchCartProducts(
        page: Int,
        size: Int,
    ): CartProducts {
        val cartItems: CartItemsResponse? =
            api
                .getCartItems(page, size)
                .execute()
                .body()

        return CartProducts(
            cartItems
                ?.content
                ?.map { it.toDomain() }
                ?: emptyList(),
            Page(page, cartItems?.first ?: true, cartItems?.last ?: true),
        )
    }

    override fun fetchCartItemCount(): Int = dao.getCartItemCount()

    override fun saveCartProduct(
        productId: Int,
        quantity: Int,
    ) {
        api.postCartItem(
            cartItemRequest =
                CartItemRequest(
                    productId = productId.toLong(),
                    quantity = quantity,
                ),
        )
    }

    override fun deleteCartProduct(productId: Int) {
        dao.deleteCartProduct(productId)
    }
}
