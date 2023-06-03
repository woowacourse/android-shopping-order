package woowacourse.shopping.data.repository.retrofit

import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.datasource.order.OrderDataSourceImpl
import woowacourse.shopping.domain.model.OrderItems
import woowacourse.shopping.domain.model.OrderResponse
import woowacourse.shopping.domain.repository.OrderProductRepository

class OrderProductRepositoryImpl : OrderProductRepository {
    private val orderProductDataSource = OrderDataSourceImpl()
    private val token: String?
        get() = ShoppingApplication.pref.getToken()

    override fun orderProduct(
        token: String,
        orderItems: OrderItems,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        orderProductDataSource.orderProducts(
            token = token,
            orderItems = orderItems,
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }

    override fun requestOrders(
        token: String,
        onSuccess: (List<OrderResponse>) -> Unit,
        onFailure: () -> Unit,
    ) {
        orderProductDataSource.requestOrders(
            token = token,
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }

    override fun requestSpecificOrder(
        token: String,
        orderId: String,
        onSuccess: (OrderResponse) -> Unit,
        onFailure: () -> Unit,
    ) {
        orderProductDataSource.requestSpecificOrder(
            token = token,
            orderId = orderId,
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }
}
