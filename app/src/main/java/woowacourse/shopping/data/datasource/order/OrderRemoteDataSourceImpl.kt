package woowacourse.shopping.data.datasource.order

import retrofit2.Call
import retrofit2.Response
import woowacourse.shopping.data.NetworkModule.AUTHORIZATION_FORMAT
import woowacourse.shopping.data.NetworkModule.encodedUserInfo
import woowacourse.shopping.data.NetworkModule.orderService
import woowacourse.shopping.data.datasource.request.OrderRequest
import woowacourse.shopping.data.datasource.response.OrderEntity

class OrderRemoteDataSourceImpl : OrderRemoteDataSource {

    override fun addOrder(orderRequest: OrderRequest): Result<Long> {
        val response = orderService.addOrder(
            authorization = AUTHORIZATION_FORMAT.format(encodedUserInfo),
            orderRequest = orderRequest
        ).execute()

        if (response.code() == 409) {
            return Result.failure(Throwable(STOCK_ERROR))
        }
        return response.headers()[LOCATION]?.run {
            Result.success(
                this.split("/")
                    .last()
                    .toLong()
            )
        } ?: Result.failure(Throwable(FAILED_TO_ADD_ORDER))
    }

    override fun getOrder(
        orderId: Int,
        onReceived: (OrderEntity) -> Unit,
        onFailed: (errorMessage: String) -> Unit,
    ) {
        orderService.getOrder(
            authorization = AUTHORIZATION_FORMAT.format(encodedUserInfo),
            orderId = orderId
        ).enqueue(object : retrofit2.Callback<OrderEntity> {

            override fun onResponse(
                call: Call<OrderEntity>,
                response: Response<OrderEntity>,
            ) {
                response.body()?.let {
                    onReceived(it)
                } ?: onFailed(ORDER_INFO_ERROR)
            }

            override fun onFailure(call: Call<OrderEntity>, t: Throwable) {
                onFailed(ORDER_INFO_ERROR)
            }
        })
    }

    override fun getOrders(
        onReceived: (List<OrderEntity>) -> Unit,
        onFailed: (errorMessage: String) -> Unit,
    ) {
        orderService.getOrders(
            authorization = AUTHORIZATION_FORMAT.format(encodedUserInfo)
        ).enqueue(object : retrofit2.Callback<List<OrderEntity>> {

            override fun onResponse(
                call: Call<List<OrderEntity>>,
                response: Response<List<OrderEntity>>,
            ) {
                response.body()?.let {
                    onReceived(it)
                } ?: onFailed(ORDERS_INFO_ERROR)
            }

            override fun onFailure(call: Call<List<OrderEntity>>, t: Throwable) {
                onFailed(ORDERS_INFO_ERROR)
            }
        })
    }

    companion object {
        private const val FAILED_TO_ADD_ORDER = "주문을 추가하지 못했습니다"
        private const val ORDER_INFO_ERROR = "주문에 대한 정보를 받아오지 못했습니다."
        private const val ORDERS_INFO_ERROR = "주문 목록에 대한 정보를 받아오지 못했습니다."
        private const val STOCK_ERROR = "상품 재고가 부족해 주문에 실패했습니다."
        private const val LOCATION = "Location"
    }
}
