package woowacourse.shopping.domain

data class ProductPage(
    val products: List<Product>,
    val isLast: Boolean,
)
