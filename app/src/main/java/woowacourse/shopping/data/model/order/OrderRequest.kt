package woowacourse.shopping.data.model.order

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderRequest(
    @SerialName("cartItemIds")
    val cartItemIds: List<Long>,
)
