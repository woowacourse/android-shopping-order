package woowacourse.shopping.data.order

import com.example.domain.FixedDiscountPolicy
import com.example.domain.order.Order
import com.example.domain.order.OrderSummary

interface OrderRepository {

    fun requestFetchAllOrders(
        success: (List<OrderSummary>) -> Unit,
        failure: () -> Unit
    )

    fun requestAddOrder(
        cartIds: List<Long>,
        finalPrice: Int,
        success: (orderId: Long) -> Unit,
        failure: () -> Unit
    )

    fun requestFetchOrderById(
        id: Long,
        success: (order: Order?) -> Unit,
        failure: () -> Unit
    )

    fun requestFetchDiscountPolicy(
        success: (fixedDiscountPolicy: FixedDiscountPolicy) -> Unit,
        failure: () -> Unit
    )
}
