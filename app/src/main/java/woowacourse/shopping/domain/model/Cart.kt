package woowacourse.shopping.domain.model

data class Cart(
    private val cartProducts: List<CartItem> = emptyList(),
) {
    private val cartMapByProductId: Map<Long, CartItem> =
        cartProducts.associateBy { it.product.productId }

    private constructor(cartMapByProductId: Map<Long, CartItem>) : this(cartMapByProductId.values.toList())

    fun add(cartItem: CartItem): Cart {
        val productId = cartItem.product.productId
        val newCartItems = cartMapByProductId.plus(productId to cartItem)
        return Cart(newCartItems)
    }

    fun exist(productId: Long): Boolean = cartMapByProductId.containsKey(productId)

    fun findCartItem(productId: Long): CartItem? = cartMapByProductId[productId]
}
