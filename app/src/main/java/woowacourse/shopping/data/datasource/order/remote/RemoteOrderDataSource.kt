package woowacourse.shopping.data.datasource.order.remote

import retrofit2.Response
import woowacourse.shopping.data.datasource.order.OrderDataSource
import woowacourse.shopping.data.httpclient.RetrofitModule
import woowacourse.shopping.data.httpclient.mapper.toData
import woowacourse.shopping.data.httpclient.request.AddOrderRequest
import woowacourse.shopping.data.httpclient.response.order.Individualorder.IndividualOrderResponse
import woowacourse.shopping.data.httpclient.response.order.addorder.AddOrderErrorBody
import woowacourse.shopping.data.httpclient.response.order.addorder.AddOrderFailureException
import woowacourse.shopping.data.model.DataOrder
import woowacourse.shopping.support.framework.data.httpclient.getParsedErrorBody
import woowacourse.shopping.support.framework.data.httpclient.getRetrofitCallback

class RemoteOrderDataSource : OrderDataSource.Remote {
    override fun addOrder(
        basketProductsId: List<Int>,
        usingPoint: Int,
        orderTotalPrice: Int,
        onReceived: (Result<Int>) -> Unit
    ) {
        val addOrderRequest: AddOrderRequest = AddOrderRequest(
            cartIds = basketProductsId,
            point = usingPoint,
            totalPrice = orderTotalPrice
        )
        RetrofitModule.orderService.addOrder(addOrderRequest).enqueue(
            getRetrofitCallback(
                failureLogTag = this::class.java.name,
                onResponse = { _, response -> addOrderRequestOnResponse(response, onReceived) }
            )
        )
    }

    private fun addOrderRequestOnResponse(
        response: Response<Unit>,
        onReceived: (Result<Int>) -> Unit
    ) {
        if (response.isSuccessful) {
            val orderId = response.headers()["Location"]?.split("/")?.last()?.toInt()
            orderId?.let { onReceived(Result.success(it)) }
        } else {
            val errorData =
                RetrofitModule.retrofit.getParsedErrorBody<AddOrderErrorBody>(response.errorBody())

            errorData?.let {
                onReceived(Result.failure(AddOrderFailureException(addOrderErrorBody = it)))
            }
        }
    }

    override fun getIndividualOrderInfo(orderId: Int, onReceived: (DataOrder) -> Unit) {
        RetrofitModule.orderService.getIndividualOrderInfo(orderId).enqueue(
            getRetrofitCallback<IndividualOrderResponse>(
                failureLogTag = this::class.java.name,
                onResponse = { _, response ->
                    val orderInfo = response.body()?.toData()
                    orderInfo?.let { onReceived(it) }
                }
            )
        )
    }

    override fun getOrdersInfo(onReceived: (List<DataOrder>) -> Unit) {
        RetrofitModule.orderService.getOrdersInfo().enqueue(
            getRetrofitCallback<List<IndividualOrderResponse>>(
                failureLogTag = this::class.java.name,
                onResponse = { _, response ->
                    val ordersInfo = response.body()?.map { it.toData() }
                    ordersInfo?.let { onReceived(it) }
                }
            )
        )
    }
}
