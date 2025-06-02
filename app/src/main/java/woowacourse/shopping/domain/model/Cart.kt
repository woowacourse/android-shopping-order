package woowacourse.shopping.domain.model

data class Cart(
    private val cartProducts: List<CartItem> = emptyList(),
) {
    val cartMapByProductId: Map<Long, CartItem> =
        cartProducts.associateBy { it.product.productId }
}
