package woowacourse.shopping.domain.model

data class ProductsPage(
    val products: List<Product>,
    val isLastPage: Boolean,
)
