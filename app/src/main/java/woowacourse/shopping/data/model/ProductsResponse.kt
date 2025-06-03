package woowacourse.shopping.data.model

data class ProductsResponse(
    val content: List<ProductResponse>,
    val pageable: Pageable,
    val last: Boolean,
    val first: Boolean,
)
