package woowacourse.shopping.data.order

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.Order
import woowacourse.shopping.OrderDetail
import woowacourse.shopping.OrderProducts
import woowacourse.shopping.data.HttpErrorHandler
import woowacourse.shopping.data.common.model.BaseResponse
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.order.model.OrderAddBody
import woowacourse.shopping.data.order.model.OrderDataModel
import woowacourse.shopping.data.order.model.OrderDetailDataModel
import woowacourse.shopping.data.order.model.OrderProductBody
import woowacourse.shopping.repository.OrderRepository

class OrderRepositoryImpl constructor(
    private val orderRemoteDataSource: OrderRemoteDataSource
) : OrderRepository {
    override fun getAllOrders(onSuccess: (List<Order>) -> Unit) {
        orderRemoteDataSource.getAllOrders()
            .enqueue(object : Callback<BaseResponse<List<OrderDataModel>>> {
                override fun onResponse(
                    call: Call<BaseResponse<List<OrderDataModel>>>,
                    response: Response<BaseResponse<List<OrderDataModel>>>
                ) {
                    val orderDataModels = response.body()?.result
                    if (orderDataModels == null) onSuccess(emptyList())
                    else {

                        val orders = orderDataModels.map { it.toDomain() }
                        onSuccess(orders)
                    }
                }

                override fun onFailure(
                    call: Call<BaseResponse<List<OrderDataModel>>>,
                    t: Throwable
                ) {
                    HttpErrorHandler.throwError(t)
                }
            })
    }

    override fun getOrderDetail(orderId: Int, onSuccess: (OrderDetail?) -> Unit) {
        orderRemoteDataSource.getOrderDetail(orderId)
            .enqueue(object : Callback<BaseResponse<OrderDetailDataModel>> {
                override fun onResponse(
                    call: Call<BaseResponse<OrderDetailDataModel>>,
                    response: Response<BaseResponse<OrderDetailDataModel>>
                ) {
                    val orderDetailDataModel = response.body()?.result
                    val orderDetail = orderDetailDataModel?.toDomain()
                    onSuccess(orderDetail)
                }

                override fun onFailure(
                    call: Call<BaseResponse<OrderDetailDataModel>>,
                    t: Throwable
                ) {
                    HttpErrorHandler.throwError(t)
                }
            })
    }

    override fun addOrder(
        sendPoint: Int,
        orderProducts: OrderProducts,
        onSuccess: (String?) -> Unit
    ) {
        val orderProductBodies = orderProducts.items.map {
            OrderProductBody(productId = it.product.id, quantity = it.count)
        }
        val orderAddBody = OrderAddBody(sendPoint, orderProductBodies)
        orderRemoteDataSource.addOrder(orderAddBody)
            .enqueue(object : Callback<BaseResponse<Unit>> {
                override fun onResponse(
                    call: Call<BaseResponse<Unit>>,
                    response: Response<BaseResponse<Unit>>
                ) {
                    val message = response.body()?.message
                    onSuccess(message)
                }

                override fun onFailure(call: Call<BaseResponse<Unit>>, t: Throwable) {
                    HttpErrorHandler.throwError(t)
                }
            })
    }
}
