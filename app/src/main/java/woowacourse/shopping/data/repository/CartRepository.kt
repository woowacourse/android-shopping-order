package woowacourse.shopping.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    ): Result<Products> =
        withContext(Dispatchers.IO) {
            runCatching {
                api.getCartItems(page, size)
            }.mapCatching { response ->
                val items = response.content.map { it.toDomain() }
                val pageInfo = Page(page, response.first, response.last)
                Products(items, pageInfo)
            }
        }

    override suspend fun fetchAllCartProducts(): Result<Products> {
        val firstPage = 0
        val maxSize = Int.MAX_VALUE

        return fetchCartProducts(firstPage, maxSize)
    }

    override suspend fun fetchCartItemCount(): Result<Int> =
        withContext(Dispatchers.IO) {
            runCatching {
                api.getCartItemsCount()
            }.mapCatching { response ->
                response.quantity
            }
        }

    override suspend fun addCartProduct(
        productId: Long,
        quantity: Int,
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                api.postCartItem(CartItemRequest(productId = productId, quantity = quantity))
            }
        }

    override suspend fun updateCartProduct(
        cartId: Long,
        quantity: Int,
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                api.patchCartItem(cartId, CartItemQuantityRequest(quantity))
            }
        }

    override suspend fun deleteCartProduct(cartId: Long): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                api.deleteCartItem(cartId)
            }
        }
}
