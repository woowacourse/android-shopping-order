package woowacourse.shopping.data.order.model

data class OrderAddBody(val spendPoint: Int, val orderItems: List<OrderProductBody>)
