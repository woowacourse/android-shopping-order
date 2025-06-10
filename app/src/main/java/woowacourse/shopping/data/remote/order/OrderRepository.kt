package woowacourse.shopping.data.remote.order

class OrderRepository(
    private val orderService: OrderService,
) {
    suspend fun order(orderRequest: OrderRequest): Result<Unit> =
        try {
            val response = orderService.order(orderRequest)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Throwable("응답 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
}
