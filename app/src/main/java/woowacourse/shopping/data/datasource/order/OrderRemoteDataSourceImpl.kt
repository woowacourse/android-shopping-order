package woowacourse.shopping.data.datasource.order

import woowacourse.shopping.data.NetworkModule.AUTHORIZATION_FORMAT
import woowacourse.shopping.data.NetworkModule.encodedUserInfo
import woowacourse.shopping.data.NetworkModule.orderService
import woowacourse.shopping.data.datasource.getResult
import woowacourse.shopping.data.datasource.getResultOnHeaders
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
        return response.getResultOnHeaders(FAILED_TO_ADD_ORDER)
    }

    override fun getOrder(orderId: Int): Result<OrderEntity> {
        val response = orderService.getOrder(
            authorization = AUTHORIZATION_FORMAT.format(encodedUserInfo),
            orderId = orderId
        ).execute()

        return response.getResult(ORDER_INFO_ERROR)
    }

    override fun getOrders(): Result<List<OrderEntity>> {
        val response = orderService.getOrders(
            authorization = AUTHORIZATION_FORMAT.format(encodedUserInfo)
        ).execute()

        return response.getResult(ORDERS_INFO_ERROR)
    }

    companion object {
        private const val FAILED_TO_ADD_ORDER = "주문을 추가하지 못했습니다"
        private const val ORDER_INFO_ERROR = "주문에 대한 정보를 받아오지 못했습니다."
        private const val ORDERS_INFO_ERROR = "주문 목록에 대한 정보를 받아오지 못했습니다."
        private const val STOCK_ERROR = "상품 재고가 부족해 주문에 실패했습니다."
    }
}
