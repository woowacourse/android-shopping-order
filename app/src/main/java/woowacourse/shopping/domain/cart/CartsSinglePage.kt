package woowacourse.shopping.domain.cart

data class CartsSinglePage(
    val carts: List<ShoppingCart>,
    val hasNextPage: Boolean,
) {
    fun isSavedInCart(productId: Long) =
        carts.find {
            it.productId == productId
        }
}
