package woowacourse.shopping.data.repository

import woowacourse.shopping.data.api.CartApi
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.model.request.CartItemQuantityRequest
import woowacourse.shopping.data.model.request.CartItemRequest
import woowacourse.shopping.domain.model.Page
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.CartRepository

class CartRepository(
    private val api: CartApi,
) : CartRepository {
    override suspend fun fetchCartProducts(
        page: Int,
        size: Int,
    ): Result<Products> =
        runCatching {
            val request = api.getCartItems(page, size)
            if (request.isSuccessful) {
                val body = request.body()
                val items = body?.content?.map { it.toDomain() } ?: emptyList()
                val pageInfo = Page(page, body?.first ?: true, body?.last ?: true)
                Products(items, pageInfo)
            } else {
                throw IllegalArgumentException()
            }
        }

    override suspend fun fetchAllCartProducts(): Result<Products> {
        val firstPage = 0
        val maxSize = Int.MAX_VALUE
        return fetchCartProducts(firstPage, maxSize)
    }

    override suspend fun fetchCartItemCount(): Result<Int> =
        runCatching {
            val request = api.getCartItemsCount()

            if (request.isSuccessful) {
                request.body()?.quantity ?: 0
            } else {
                throw IllegalArgumentException()
            }
        }

    override suspend fun addCartProduct(
        productId: Long,
        quantity: Int,
    ): Result<Unit> =
        runCatching {
            val request = CartItemRequest(productId = productId, quantity = quantity)
            val postCartItem = api.postCartItem(request)

            if (postCartItem.isSuccessful) {
                Unit
            } else {
                throw IllegalArgumentException()
            }
        }

    override suspend fun updateCartProduct(
        cartId: Long,
        quantity: Int,
    ): Result<Unit> =
        runCatching {
            val request = CartItemQuantityRequest(quantity)
            val patchCartItem = api.patchCartItem(cartId, request)

            if (patchCartItem.isSuccessful) {
                Unit
            } else {
                throw IllegalArgumentException()
            }
        }

    override suspend fun deleteCartProduct(cartId: Long): Result<Unit> =
        runCatching {
            val request = api.deleteCartItem(cartId)

            if (request.isSuccessful) {
                Unit
            } else {
                throw IllegalArgumentException()
            }
        }
}
