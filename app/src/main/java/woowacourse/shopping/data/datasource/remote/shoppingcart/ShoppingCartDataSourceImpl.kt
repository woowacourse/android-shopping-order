package woowacourse.shopping.data.datasource.remote.shoppingcart

import woowacourse.shopping.data.datasource.local.AuthInfoDataSource
import woowacourse.shopping.data.remote.ServicePool
import woowacourse.shopping.data.remote.request.CartItemRequest
import woowacourse.shopping.data.remote.request.CartProductDTO
import java.util.concurrent.Executors

class ShoppingCartDataSourceImpl(private val authInfoDataSource: AuthInfoDataSource) :
    ShoppingCartDataSource {

    private val token = authInfoDataSource.getAuthInfo() ?: throw IllegalArgumentException()

    override fun getAllProductInCart(): Result<List<CartProductDTO>> {
        val executor = Executors.newSingleThreadExecutor()
        val result = executor.submit<Result<List<CartProductDTO>>> {
            val response = ServicePool.shoppingCartService.getAllProductInCart(token).execute()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Throwable(response.message()))
            }
        }.get()
        executor.shutdown()
        return result
    }

    override fun postProductToCart(productId: Long, quantity: Int): Result<Unit> {
        val executor = Executors.newSingleThreadExecutor()
        val result = executor.submit<Result<Unit>> {
            val response =
                ServicePool.shoppingCartService.postProductToCart(token, CartItemRequest(productId, quantity)).execute()
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Throwable(response.message()))
            }
        }.get()
        executor.shutdown()
        return result
    }

    override fun patchProductCount(cartItemId: Long, quantity: Int): Result<Unit> {
        val executor = Executors.newSingleThreadExecutor()
        val result = executor.submit<Result<Unit>> {
            val response =
                ServicePool.shoppingCartService.patchProductCount(token, cartItemId, quantity)
                    .execute()
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Throwable(response.message()))
            }
        }.get()
        executor.shutdown()
        return result
    }

    override fun deleteProductInCart(productId: Long): Result<Unit> {
        val executor = Executors.newSingleThreadExecutor()
        val result = executor.submit<Result<Unit>> {
            val response =
                ServicePool.shoppingCartService.deleteProductInCart(token, productId).execute()
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Throwable(response.message()))
            }
        }.get()
        executor.shutdown()
        return result
    }
}
