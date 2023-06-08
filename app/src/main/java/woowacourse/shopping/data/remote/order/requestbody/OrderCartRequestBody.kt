package woowacourse.shopping.data.remote.order.requestbody

data class OrderCartRequestBody(
    val productId: Int,
    val quantity: Int
)
