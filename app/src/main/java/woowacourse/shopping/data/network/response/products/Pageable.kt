package woowacourse.shopping.data.network.response.products

import kotlinx.serialization.SerialName

@Serializable
data class Pageable(
    val offset: Int,
    val pageNumber: Int,
    val pageSize: Int,
    val paged: Boolean,
    val sort: SortX,
    @SerialName("unpaged")
    val unPaged: Boolean,
)
