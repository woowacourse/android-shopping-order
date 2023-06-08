package woowacourse.shopping.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderListDto(
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int,
    val contents: List<OrderDto>
)
