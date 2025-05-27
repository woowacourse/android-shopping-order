package woowacourse.shopping.data.model.request

import kotlinx.serialization.SerialName

data class Pageable(
    @SerialName("page") val page: Int,
    @SerialName("size") val size: Int,
    @SerialName("sort") val sort: List<String>
)
