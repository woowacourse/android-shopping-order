package woowacourse.shopping.model

class CartProducts(cartProducts: List<CartProduct>) {
    private val cartProducts: MutableList<CartProduct>
    private var checks = mutableMapOf<Int, Boolean>()
    private val checkedProducts: List<CartProduct>
        get() = cartProducts.filter { checks.getOrPut(it.id) { true } }
    val size: Int get() = cartProducts.size

    val totalPrice: Int
        get() = checkedProducts.sumOf { it.product.price * it.quantity }

    val totalQuantity: Int
        get() = cartProducts.sumOf { it.quantity }

    val totalCheckedQuantity: Int
        get() = checkedProducts.sumOf { it.quantity }

    init {
        checks = cartProducts.associateBy({ it.id }, { true }).toMutableMap()
        this.cartProducts = cartProducts.toMutableList()
    }

    operator fun get(index: Int): CartProduct {
        return cartProducts[index]
    }

    fun all(): List<CartProduct> {
        return cartProducts.toList()
    }

    fun changeChecked(id: Int, checked: Boolean) {
        checks[id] = checked
    }

    fun getCheckedById(id: Int): Boolean {
        return checks[id] ?: false
    }

    fun findByProductId(productId: Int): CartProduct? {
        return cartProducts.firstOrNull { it.product.id == productId }
    }

    fun replaceAll(cartProducts: List<CartProduct>) {
        this.cartProducts.clear()
        this.cartProducts.addAll(cartProducts)
        cartProducts.forEach { checks.putIfAbsent(it.id, true) }
    }

    fun isEmpty(): Boolean {
        return cartProducts.isEmpty()
    }

    fun subList(offset: Int, size: Int): CartProducts {
        val lastIndex = cartProducts.lastIndex
        val endIndex = (lastIndex + 1).coerceAtMost(offset + size)

        return when (offset) {
            in 0..lastIndex -> CartProducts(cartProducts.subList(offset, endIndex))
            else -> CartProducts(emptyList())
        }
    }

    fun hasNextPage(index: Int, size: Int): Boolean {
        return index * size + size < cartProducts.size
    }

    fun hasPrevPage(index: Int, size: Int): Boolean {
        return index * size - size >= 0
    }
}
