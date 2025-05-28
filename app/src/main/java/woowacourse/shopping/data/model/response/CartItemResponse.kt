package woowacourse.shopping.data.model.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartItemResponse(
    @SerialName("content")
    val content: List<CartItemContent>,
    @SerialName("empty")
    val empty: Boolean,
    @SerialName("first")
    val first: Boolean,
    @SerialName("last")
    val last: Boolean,
    @SerialName("number")
    val number: Int,
    @SerialName("numberOfElements")
    val numberOfElements: Int,
    @SerialName("pageable")
    val pageable: Pageable,
    @SerialName("size")
    val size: Int,
    @SerialName("sort")
    val sort: Sort,
    @SerialName("totalElements")
    val totalElements: Long,
    @SerialName("totalPages")
    val totalPages: Int
)