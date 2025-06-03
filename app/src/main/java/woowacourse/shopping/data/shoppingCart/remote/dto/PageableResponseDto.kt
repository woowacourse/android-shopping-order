package woowacourse.shopping.data.shoppingCart.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PageableResponseDto(
    @SerialName("offset")
    val offset: Int,
    @SerialName("pageNumber")
    val pageNumber: Int,
    @SerialName("pageSize")
    val pageSize: Int,
    @SerialName("paged")
    val paged: Boolean,
    @SerialName("sort")
    val sort: SortResponseDto,
    @SerialName("unpaged")
    val unPaged: Boolean,
)
