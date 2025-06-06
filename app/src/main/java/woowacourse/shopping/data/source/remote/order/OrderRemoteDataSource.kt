package woowacourse.shopping.data.source.remote.order

import woowacourse.shopping.data.model.OrderRequest
import woowacourse.shopping.data.source.remote.api.OrderApiService

class OrderRemoteDataSource(
    private val api: OrderApiService,
) : OrderDataSource {
    override suspend fun orderCheckedItems(cartIds: List<Long>): Result<Unit> {
        val request = OrderRequest(cartItemIds = cartIds)
        val response = api.postOrders(request = request)

        return if (response.isSuccessful)
            {
                Result.success(Unit)
            } else {
            Result.failure(Exception(POST_ERROR_MESSAGE))
        }
    }

    companion object {
        private const val POST_ERROR_MESSAGE = "[ERROR] 주문이 실패했습니다."
    }
}
