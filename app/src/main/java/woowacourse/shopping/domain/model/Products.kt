package woowacourse.shopping.domain.model

data class Products(
    val content: List<Product>,
    val pageable: Pageable,
)
