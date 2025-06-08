package woowacourse.shopping.data.order.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderRequestBody(
    @SerialName("cartItemIds")
    val cartItemIds: List<Int>
)