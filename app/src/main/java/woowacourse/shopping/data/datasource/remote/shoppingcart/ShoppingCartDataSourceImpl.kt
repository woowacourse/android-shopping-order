package woowacourse.shopping.data.datasource.remote.shoppingcart

import woowacourse.shopping.data.datasource.remote.retrofit.RetrofitClient
import woowacourse.shopping.data.remote.request.CartItemRequest
import woowacourse.shopping.data.remote.request.CartProductDTO
import java.util.concurrent.Executors

class ShoppingCartDataSourceImpl :
    ShoppingCartDataSource {

    override fun getAllProductInCart(): Result<List<CartProductDTO>> {
        val executor = Executors.newSingleThreadExecutor()
        val result = executor.submit<Result<List<CartProductDTO>>> {
            val response =
                RetrofitClient.getInstance().shoppingCartService.getAllProductInCart().execute()
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
                RetrofitClient.getInstance().shoppingCartService.postProductToCart(
                    CartItemRequest(productId, quantity),
                ).execute()
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
                RetrofitClient.getInstance().shoppingCartService.patchProductCount(
                    cartItemId,
                    quantity,
                )
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
                RetrofitClient.getInstance().shoppingCartService.deleteProductInCart(productId)
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
}
