package woowacourse.shopping.domain.cart

data class CartsSinglePage(
    val carts: ShoppingCarts,
    val hasNextPage: Boolean,
) {
    fun isSavedInCart(productId: Long) = carts.findProduct(productId)
}
