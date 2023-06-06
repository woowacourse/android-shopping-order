package woowacourse.shopping.domain.model

data class ProductsWithCartItem(
    val products: List<ProductWithCartInfo>,
    val last: Boolean,
)
