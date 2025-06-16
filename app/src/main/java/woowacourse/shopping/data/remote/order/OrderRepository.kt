package woowacourse.shopping.data.remote.order

import retrofit2.HttpException

class OrderRepository(
    private val orderService: OrderService,
) {
    suspend fun fetchOrder(cartId: List<Long>): Result<Unit> =
        try {
            val response = orderService.requestOrder(orderRequest = OrderRequest(cartId))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
}
