package woowacourse.shopping.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class Pageable(
    val page: Int,
    val size: Int,
    val sort: List<String>,
)
