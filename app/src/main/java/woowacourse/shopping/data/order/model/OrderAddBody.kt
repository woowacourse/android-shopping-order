package woowacourse.shopping.data.order.model

data class OrderAddBody(val sendPoint: Int, val orderItems: List<OrderProductBody>)
