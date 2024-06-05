package woowacourse.shopping.domain.entity

data class Cart(
    val cartProducts: List<CartProduct> = emptyList(),
) {
    private val cartMapByProductId: Map<Long, CartProduct>
        get() = cartProducts.associateBy { it.product.id }

    private constructor(cartMapByProductId: Map<Long, CartProduct>) : this(
        cartMapByProductId.values.toList()
    )

    constructor (vararg cartProducts: CartProduct) : this(cartProducts.toList())

    fun hasProductId(productId: Long): Boolean = cartMapByProductId.containsKey(productId)

    fun addAll(cart: Cart): Cart {
        val newCartProducts = cartMapByProductId + cart.cartMapByProductId
        return Cart(newCartProducts)
    }

    fun add(cartProduct: CartProduct): Cart {
        val productId = cartProduct.product.id
        val newCartProducts = cartMapByProductId.plus(productId to cartProduct)
        return Cart(newCartProducts)
    }

    fun isEmpty(): Boolean = cartProducts.isEmpty()

    @Deprecated("Use add(cartProduct: CartProduct) instead")
    fun add(product: Product): Cart {
        val productId = product.id
        require(hasProductId(productId).not()) { ERROR_ALREADY_EXIST_PRODUCT.format(product) }
        val newCartProduct = CartProduct(product, 1)
        return Cart(cartProducts + newCartProduct)
    }

    fun filterByProductIds(productIds: List<Long>): Cart {
        val filteredProducts = cartProducts.filter { it.product.id in productIds }
        return Cart(filteredProducts)
    }

    fun findCartProductByProductId(productId: Long): CartProduct? = cartMapByProductId[productId]

    fun delete(productId: Long): Cart {
        findCartProductByProductId(productId) ?: throw IllegalArgumentException(
            ERROR_NOT_EXIST_PRODUCT.format(productId)
        )
        val newCartProducts = cartMapByProductId - productId
        return Cart(newCartProducts)
    }

    fun increaseProductCount(productId: Long, amount: Int = 1): Cart {
        val cartProduct = findCartProductByProductId(productId) ?: throw IllegalArgumentException(
            ERROR_NOT_EXIST_PRODUCT.format(productId)
        )
        val newCartProduct = cartProduct.increaseCount(amount)
        val newProductMap = cartMapByProductId.plus(productId to newCartProduct)
        return Cart(newProductMap)
    }

    fun canDecreaseProductCount(productId: Long, amount: Int = 1): Boolean {
        val cartProduct = findCartProductByProductId(productId) ?: return false
        return cartProduct.canDecreaseCount(amount)
    }

    fun decreaseProductCount(productId: Long, amount: Int = 1): Cart {
        val cartProduct = findCartProductByProductId(productId) ?: throw IllegalArgumentException(
            ERROR_NOT_EXIST_PRODUCT.format(productId)
        )
        val newCartProduct = cartProduct.decreaseCount(amount)
        val newProductMap = cartMapByProductId.plus(productId to newCartProduct)
        return Cart(newProductMap)
    }

    companion object {
        private const val ERROR_ALREADY_EXIST_PRODUCT = "이미 존재하는 상품입니다. %s"
        private const val ERROR_NOT_EXIST_PRODUCT = "해당 상품이 존재하지 않습니다. %s"
    }
}
