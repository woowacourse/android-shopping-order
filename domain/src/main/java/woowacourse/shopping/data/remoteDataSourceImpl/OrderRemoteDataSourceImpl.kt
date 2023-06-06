package woowacourse.shopping.data.remoteDataSourceImpl

import woowacourse.shopping.data.client.RetrofitClient
import woowacourse.shopping.data.remoteDataSource.OrderRemoteDataSource
import woowacourse.shopping.dto.PostOrderRequestDto
import woowacourse.shopping.model.Order
import woowacourse.shopping.model.OrderHistories
import woowacourse.shopping.model.OrderHistory

class OrderRemoteDataSourceImpl : OrderRemoteDataSource {
    override fun getOrder(cartIds: List<Int>): Result<Order> = runCatching {
        RetrofitClient.getInstance().retrofitOrderService
            .getOrderList(cartIds.joinToString(","))
            .execute().body()!!
    }

    override fun getOrderHistoriesNext(lastOrderId: Long): Result<OrderHistories> {
        return when (lastOrderId) {
            0L -> getOrderHistoriesFirst()
            else -> getOrderHistories(lastOrderId)
        }
    }

    private fun getOrderHistoriesFirst(): Result<OrderHistories> = runCatching {
        RetrofitClient.getInstance().retrofitOrderService
            .getOrders().execute().body()!!.toDomain()
    }

    private fun getOrderHistories(lastOrderId: Long): Result<OrderHistories> = runCatching {
        RetrofitClient.getInstance().retrofitOrderService
            .getOrdersNext(lastOrderId).execute().body()!!.toDomain()
    }

    override fun getOrderHistory(id: Long): Result<OrderHistory> = runCatching {
        RetrofitClient.getInstance().retrofitOrderService
            .getOrder(id).execute().body()!!.toDomain()
    }

    override fun postOrder(point: Int, cartIds: List<Int>): Result<Long> = runCatching {
        RetrofitClient.getInstance().retrofitOrderService
            .postOrder(PostOrderRequestDto(point, cartIds))
            .execute().headers()["Location"]!!.split("/").last().toLong()
    }
}
