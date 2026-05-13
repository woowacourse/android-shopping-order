package woowacourse.shopping.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.data.source.remote.dto.Sort

@Serializable
data class Pageable(
    @SerialName("offset")
    val offset: Int,
    @SerialName("pageNumber")
    val pageNumber: Int,
    @SerialName("pageSize")
    val pageSize: Int,
    @SerialName("paged")
    val paged: Boolean,
    @SerialName("sort")
    val sort: Sort,
    @SerialName("unpaged")
    val unpaged: Boolean,
)
