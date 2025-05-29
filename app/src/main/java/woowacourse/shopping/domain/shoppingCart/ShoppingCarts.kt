package woowacourse.shopping.domain.shoppingCart

data class ShoppingCarts(
    val last: Boolean,
    val shoppingCartItems: List<ShoppingCartProduct>,
)
