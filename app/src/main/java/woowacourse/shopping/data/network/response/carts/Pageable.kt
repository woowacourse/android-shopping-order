package woowacourse.shopping.data.network.response.carts


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pageable(
    val offset: Int,
    val pageNumber: Int,
    val pageSize: Int,
    val paged: Boolean,
    val sort: SortX,
    @SerialName("unpaged")
    val unPaged: Boolean
)
