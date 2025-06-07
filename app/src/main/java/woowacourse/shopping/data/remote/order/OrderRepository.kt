package woowacourse.shopping.data.remote.order

import woowacourse.shopping.data.remote.NetworkClient

class OrderRepository {
    suspend fun order(orderRequest: OrderRequest): Result<Unit> =
        try {
            val response = NetworkClient.getOrderService().order(orderRequest)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Throwable("응답 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
}
