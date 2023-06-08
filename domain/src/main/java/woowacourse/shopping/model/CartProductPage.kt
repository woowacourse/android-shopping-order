package woowacourse.shopping.model

data class CartProductPage(
    val cartProducts: List<CartProduct>,
    val firstPage: Boolean,
    val lastPage: Boolean,
    val currentPage: Int
)
