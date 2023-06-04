package woowacourse.shopping.data.remoteDataSourceImpl

import woowacourse.shopping.data.remoteDataSource.OrderRemoteDataSource
import woowacourse.shopping.model.Order
import woowacourse.shopping.model.OrderHistory
import woowacourse.shopping.model.PostOrderRequest
import woowacourse.shopping.utils.RetrofitUtil

class OrderRemoteDataSourceImpl : OrderRemoteDataSource {
    private var credentials = "Basic YUBhLmNvbToxMjM0"

    private var lastOrderId: Long = 0

    override fun getOrder(cartIds: List<Int>): Result<Order> = runCatching {
        RetrofitUtil.retrofitOrderService.getOrderList(credentials, cartIds.joinToString(","))
            .execute().body()!!
    }

    override fun getOrderHistoriesNext(): Result<List<OrderHistory>> {
        return when (lastOrderId) {
            0L -> getOrderHistoriesFirst()
            else -> getOrderHistories()
        }
    }

    private fun getOrderHistoriesFirst(): Result<List<OrderHistory>> = runCatching {
        RetrofitUtil.retrofitOrderService.getOrders(credentials).execute().body()!!
            .let {
                lastOrderId = it.lastOrderId
                return Result.success(it.orderHistories)
            }
    }

    private fun getOrderHistories(): Result<List<OrderHistory>> = runCatching {
        RetrofitUtil.retrofitOrderService.getOrdersNext(credentials, lastOrderId).execute().body()!!
            .let {
                lastOrderId = it.lastOrderId
                return Result.success(it.orderHistories)
            }
    }

    override fun getOrderHistory(id: Long): Result<OrderHistory> = runCatching {
        RetrofitUtil.retrofitOrderService.getOrder(credentials, id).execute().body()!!
    }

    override fun postOrder(point: Int, cartIds: List<Int>): Result<Long> = runCatching {
        RetrofitUtil.retrofitOrderService.postOrder(credentials, PostOrderRequest(point, cartIds))
            .execute().headers().get("Location")!!.split("/").last().toLong()
    }
}
