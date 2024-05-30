package woowacourse.shopping.data.order.remote

data class CreateOrderRequest(
    val cartItemIds: List<Int>,
)
