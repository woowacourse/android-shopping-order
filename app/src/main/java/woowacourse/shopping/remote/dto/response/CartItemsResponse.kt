package woowacourse.shopping.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartItemsResponse(
    @SerialName("content")
    val cartItems: List<CartItemResponse>,
    @SerialName("pageable")
    val pageConfig: PageConfigResponse,
    @SerialName("numberOfElements")
    val productSize: Int,
    @SerialName("totalElements")
    val totalProductSize: Int,
    @SerialName("totalPages")
    val totalPageSize: Int,
    @SerialName("empty")
    val isEmpty: Boolean = false,
    @SerialName("first")
    val isFirst: Boolean = false,
    @SerialName("last")
    val isLast: Boolean = false,
)