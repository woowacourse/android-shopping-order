package woowacourse.shopping.data.remoteDataSourceImpl

import woowacourse.shopping.data.remoteDataSource.OrderRemoteDataSource
import woowacourse.shopping.dto.PostOrderRequestDto
import woowacourse.shopping.model.Order
import woowacourse.shopping.model.OrderHistory
import woowacourse.shopping.utils.RetrofitUtil

class OrderRemoteDataSourceImpl : OrderRemoteDataSource {
    private var lastOrderId: Long = 0

    override fun getOrder(cartIds: List<Int>): Result<Order> = runCatching {
        RetrofitUtil.getInstance().retrofitOrderService
            .getOrderList(cartIds.joinToString(","))
            .execute().body()!!
    }

    override fun getOrderHistoriesNext(): Result<List<OrderHistory>> {
        return when (lastOrderId) {
            0L -> getOrderHistoriesFirst()
            else -> getOrderHistories()
        }
    }

    private fun getOrderHistoriesFirst(): Result<List<OrderHistory>> = runCatching {
        RetrofitUtil.getInstance().retrofitOrderService
            .getOrders().execute().body()!!
            .let { response ->
                lastOrderId = response.lastOrderId
                return Result.success(response.orderHistories.map { it.toDomain() })
            }
    }

    private fun getOrderHistories(): Result<List<OrderHistory>> = runCatching {
        RetrofitUtil.getInstance().retrofitOrderService
            .getOrdersNext(lastOrderId).execute().body()!!
            .let { response ->
                lastOrderId = response.lastOrderId
                return Result.success(response.orderHistories.map { it.toDomain() })
            }
    }

    override fun getOrderHistory(id: Long): Result<OrderHistory> = runCatching {
        RetrofitUtil.getInstance().retrofitOrderService
            .getOrder(id).execute().body()!!.toDomain()
    }

    override fun postOrder(point: Int, cartIds: List<Int>): Result<Long> = runCatching {
        RetrofitUtil.getInstance().retrofitOrderService
            .postOrder(PostOrderRequestDto(point, cartIds))
            .execute().headers()["Location"]!!.split("/").last().toLong()
    }
}
