package woowacourse.shopping.repository.query

data class CartPageResult(
    val items: List<CartPageItem>,
    val totalElements: Int,
    val totalPages: Int,
    val page: Int,
)
