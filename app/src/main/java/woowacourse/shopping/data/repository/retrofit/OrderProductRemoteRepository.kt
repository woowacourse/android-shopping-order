package woowacourse.shopping.data.repository.retrofit

import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.datasource.order.OrderRemoteDataSource
import woowacourse.shopping.data.mapper.toData
import woowacourse.shopping.domain.model.OrderRequest
import woowacourse.shopping.domain.model.OrderResponse
import woowacourse.shopping.domain.model.page.Page
import woowacourse.shopping.domain.repository.OrderProductRepository

class OrderProductRemoteRepository : OrderProductRepository {
    private val orderProductDataSource = OrderRemoteDataSource()
    private val token: String?
        get() = ShoppingApplication.pref.getToken()

    override fun orderProduct(
        orderRequest: OrderRequest,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        orderProductDataSource.orderProducts(
            token = token!!,
            orderRequest = orderRequest,
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }

    override fun requestOrders(
        page: Page,
        onSuccess: (List<OrderResponse>) -> Unit,
        onFailure: () -> Unit,
    ) {
        orderProductDataSource.requestOrders(
            token = token!!,
            page = page.toData(),
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }

    override fun requestSpecificOrder(
        orderId: String,
        onSuccess: (OrderResponse) -> Unit,
        onFailure: () -> Unit,
    ) {
        orderProductDataSource.requestSpecificOrder(
            token = token!!,
            orderId = orderId,
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }
}
