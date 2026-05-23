package woowacourse.shopping.data.remote.server.dto.order

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderRequest(
    @SerialName("cartItemIds")
    val ids: List<Long>
) {

}