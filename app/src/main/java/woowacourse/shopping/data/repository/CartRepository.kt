package woowacourse.shopping.data.repository

import woowacourse.shopping.data.api.CartApi
import woowacourse.shopping.data.dao.CartDao
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.model.request.CartItemQuantityRequest
import woowacourse.shopping.data.model.request.CartItemRequest
import woowacourse.shopping.data.model.response.CartItemsResponse
import woowacourse.shopping.domain.model.Page
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.CartRepository

class CartRepository(
    private val dao: CartDao,
    private val api: CartApi,
) : CartRepository {
    override fun fetchCartProductDetail(productId: Long): Product? = dao.getCartProductDetailById(productId)?.toDomain()

    override fun fetchCartProducts(
        page: Int,
        size: Int,
    ): Products {
        val cartItems: CartItemsResponse? =
            api
                .getCartItems(page, size)
                .execute()
                .body()

        return Products(
            cartItems
                ?.content
                ?.map { it.toDomain() }
                ?: emptyList(),
            Page(page, cartItems?.first ?: true, cartItems?.last ?: true),
        )
    }

    override fun fetchAllCartProducts(): Products {
        val firstPage: Int = 0
        val maxSize: Int = Int.MAX_VALUE
        return fetchCartProducts(firstPage, maxSize)
    }

    override fun fetchCartItemCount(): Int =
        api
            .getCartItemsCount()
            .execute()
            .body()
            ?.quantity ?: 0

    override fun addCartProduct(
        id: Long,
        quantity: Int,
    ) {
        api
            .postCartItem(
                cartItemRequest =
                    CartItemRequest(
                        productId = id,
                        quantity = quantity,
                    ),
            ).execute()
    }

    override fun updateCartProduct(
        id: Long,
        quantity: Int,
    ) {
        api
            .patchCartItem(
                id = id,
                cartItemQuantityRequest = CartItemQuantityRequest(quantity),
            ).execute()
    }

    override fun deleteCartProduct(id: Long) {
        api.deleteCartItem(id).execute()
    }
}
