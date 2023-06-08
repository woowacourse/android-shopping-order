package woowacourse.shopping.data.model.order

import kotlinx.serialization.Serializable

@Serializable
data class OrderHistoryInfoDto(
    val contents: List<ContentDto>,
    val currentPage: Int,
    val pageSize: Int,
    val totalPages: Int
)
