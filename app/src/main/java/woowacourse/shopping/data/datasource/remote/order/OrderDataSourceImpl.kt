package woowacourse.shopping.data.datasource.remote.order

import com.example.domain.model.order.Order
import com.example.domain.model.order.OrderDetailProduct
import com.example.domain.model.order.OrderHistoryInfo
import com.example.domain.model.point.Point
import woowacourse.shopping.data.datasource.remote.RetrofitService
import woowacourse.shopping.data.model.order.OrderDetailDto
import woowacourse.shopping.data.model.order.OrderDetailProductDto
import woowacourse.shopping.data.model.order.OrderHistoryInfoDto
import woowacourse.shopping.data.model.order.OrderProductDto
import woowacourse.shopping.data.model.toDomain

class OrderDataSourceImpl : OrderRemoteDataSource {

    private val orderService = RetrofitService.orderService

    override fun addOrder(
        usedPoint: Point,
        orderDetailProduct: List<OrderDetailProduct>,
        callback: (Result<Unit>) -> Unit
    ) {
        val orderProducts = orderDetailProduct.map {
            OrderProductDto(
                it.product.id.toInt(),
                it.quantity
            )
        }
        val orderInfo = OrderDetailProductDto(usedPoint.value, orderProducts)
        orderService.addOrder(
            orderInfo
        ).enqueue(object : retrofit2.Callback<Unit> {
            override fun onResponse(
                call: retrofit2.Call<Unit>,
                response: retrofit2.Response<Unit>
            ) {
                if (response.code() >= 400) return onFailure(call, Throwable("문제가 발생하였습니다."))
                callback(Result.success(Unit))
            }

            override fun onFailure(call: retrofit2.Call<Unit>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    override fun cancelOrder(
        orderId: Int,
        callback: (Result<Unit>) -> Unit
    ) {
        orderService.cancelOrder(
            orderId
        ).enqueue(object : retrofit2.Callback<Unit> {
            override fun onResponse(
                call: retrofit2.Call<Unit>,
                response: retrofit2.Response<Unit>
            ) {
                if (response.code() >= 400) return onFailure(call, Throwable("문제가 발생하였습니다."))
                callback(Result.success(Unit))
            }

            override fun onFailure(call: retrofit2.Call<Unit>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    override fun loadAll(
        pageNum: Int,
        callback: (Result<OrderHistoryInfo>) -> Unit
    ) {
        orderService.requestOrderHistory(
            pageNum
        ).enqueue(object : retrofit2.Callback<OrderHistoryInfoDto> {
            override fun onResponse(
                call: retrofit2.Call<OrderHistoryInfoDto>,
                response: retrofit2.Response<OrderHistoryInfoDto>
            ) {
                if (response.code() >= 400) return onFailure(call, Throwable("문제가 발생하였습니다."))
                val result = response.body()
                if (result != null) {
                    callback(Result.success(result.toDomain()))
                }
            }

            override fun onFailure(call: retrofit2.Call<OrderHistoryInfoDto>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }

    override fun loadDetail(
        detailId: Int,
        callback: (Result<Order>) -> Unit
    ) {
        orderService.requestOrderDetail(
            detailId
        ).enqueue(object : retrofit2.Callback<OrderDetailDto> {
            override fun onResponse(
                call: retrofit2.Call<OrderDetailDto>,
                response: retrofit2.Response<OrderDetailDto>
            ) {
                if (response.code() >= 400) return onFailure(call, Throwable("문제가 발생하였습니다."))
                val result = response.body()
                if (result != null) {
                    callback(Result.success(result.toDomain()))
                }
            }

            override fun onFailure(call: retrofit2.Call<OrderDetailDto>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }
}
