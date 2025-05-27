package woowacourse.shopping.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PageableResponse<T>(
    val items: List<T>,
    val hasMore: Boolean,
)
