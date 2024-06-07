package woowacourse.shopping.data.cart.datasource

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import woowacourse.shopping.data.cart.model.CartPageData
import woowacourse.shopping.data.cart.toData
import woowacourse.shopping.data.util.executeAsResult
import woowacourse.shopping.remote.dto.request.CartItemRequest
import woowacourse.shopping.remote.dto.request.UpdateCartCountRequest
import woowacourse.shopping.remote.service.CartService
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService

class CartDataSourceImpl(
    private val ioExecutors: ExecutorService,
    private val cartService: CartService,
) : CartDataSource {
    override suspend fun loadCarts(
        currentPage: Int,
        productSize: Int,
    ): Result<CartPageData> {
        val result =
            withContext(Dispatchers.IO) {
                cartService.fetchCartItems(currentPage, productSize).executeAsResult()
                    .mapCatching { it.toData() }
            }
        return result
    }

    override fun loadTotalCarts(): Result<CartPageData> {
        return ioExecutors.submit(
            Callable {
                val totalCountResult = cartService.fetchCartItemCount().executeAsResult()
                if (totalCountResult.isSuccess) {
                    val totalCount = totalCountResult.getOrThrow().quantity
                    cartService.fetchCartItems(0, totalCount)
                        .executeAsResult()
                        .mapCatching { it.toData() }
                } else {
                    error("Failed to fetch total cart count")
                }
            },
        ).get()
    }

    override fun createCartProduct(
        productId: Long,
        count: Int,
    ): Result<Long> {
        return runCatching {
            ioExecutors.submit(
                Callable {
                    val request = CartItemRequest(productId, count)
                    val response = cartService.createCartItems(request).execute()
                    val id = response.toIdOrNull() ?: error("Failed to create cart product")
                    id
                },
            ).get()
        }
    }

    override suspend fun updateCartCount(
        cartId: Long,
        count: Int,
    ): Result<Unit> {
        val result =
            withContext(Dispatchers.IO) {
                cartService.patchCartItem(cartId, UpdateCartCountRequest(count)).executeAsResult()
            }
        return result
    }

    override fun deleteCartProduct(cartId: Long): Result<Unit> {
        return ioExecutors.submit(
            Callable {
                cartService.deleteCartItem(cartId).executeAsResult()
            },
        ).get()
    }

    private fun <T> Response<T>.toIdOrNull(): Long? {
        return headers()["LOCATION"]?.substringAfterLast("/")
            ?.toLongOrNull()
    }
}
