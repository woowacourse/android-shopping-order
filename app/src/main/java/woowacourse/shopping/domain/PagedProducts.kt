package woowacourse.shopping.domain

data class PagedProducts(
    val products: List<Product>,
    val isLast: Boolean,
)
