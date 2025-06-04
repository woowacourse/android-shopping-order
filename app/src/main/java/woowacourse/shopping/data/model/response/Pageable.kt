package woowacourse.shopping.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pageable(
    @SerialName("offset") val offset: Long,
    @SerialName("pageNumber") val pageNumber: Int,
    @SerialName("pageSize") val pageSize: Int,
    @SerialName("paged") val paged: Boolean,
    @SerialName("sort") val sort: Sort,
    @SerialName("unpaged") val unpaged: Boolean,
)
