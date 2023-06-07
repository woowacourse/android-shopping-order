package woowacourse.shopping.data.order.response

import kotlinx.serialization.Serializable

@Serializable
data class PostOrderResponse(val orderId: Int)