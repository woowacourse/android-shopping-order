package woowacourse.shopping.domain.cart

data class CartsSinglePage(
    val products: List<ShoppingCart>,
    val hasNextPage: Boolean,
)
