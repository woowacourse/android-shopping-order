package woowacourse.shopping.data.model

data class OrderListDto(
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int,
    val contents: List<OrderDto>
)
