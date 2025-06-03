package woowacourse.shopping.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartProductResponseDto(
    @SerialName("totalElements")
    val totalElements: Long,
    @SerialName("totalPages")
    val totalPages: Int,
    @SerialName("size")
    val size: Int,
    @SerialName("content")
    val content: List<CartProductDto>,
    @SerialName("number")
    val number: Int,
    @SerialName("sort")
    val sort: SortDto,
    @SerialName("pageable")
    val pageable: PageableDto,
    @SerialName("first")
    val first: Boolean,
    @SerialName("last")
    val last: Boolean,
    @SerialName("numberOfElements")
    val numberOfElements: Int,
    @SerialName("empty")
    val empty: Boolean,
)
