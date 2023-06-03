package woowacourse.shopping.model

class CartProducts(private val value: MutableList<CartProduct>) {
    private val checks = value.associateBy({ it.id }, { true }).toMutableMap()

    val checkedProducts: List<CartProduct>
        get() = value.filter { checks.getOrPut(it.id) { true } }

    val totalPrice: Int
        get() = checkedProducts.sumOf { it.product.price * it.quantity }

    val totalCheckedQuantity: Int
        get() = checkedProducts.sumOf { it.quantity }

    val totalQuantity: Int
        get() = value.sumOf { it.quantity }

    val size: Int get() = value.size

    operator fun get(index: Int): CartProduct {
        return value[index]
    }

    fun toList(): List<CartProduct> {
        return value.toList()
    }

    fun map(transform: (CartProduct) -> CartProduct): CartProducts {
        return CartProducts(value.map(transform).toMutableList())
    }

    fun changeChecked(id: Int, checked: Boolean) {
        checks[id] = checked
    }

    fun getCheck(id: Int): Boolean {
        return checks.getOrDefault(id, false)
    }

    fun findByProductId(productId: Int): CartProduct? {
        return value.firstOrNull { it.product.id == productId }
    }

    fun replaceAll(cartProducts: List<CartProduct>) {
        this.value.clear()
        this.value.addAll(cartProducts)
        cartProducts.forEach { checks.putIfAbsent(it.id, true) }
    }

    fun isEmpty(): Boolean {
        return value.isEmpty()
    }

    fun subList(offset: Int, size: Int): CartProducts {
        val lastIndex = value.lastIndex
        val endIndex = (lastIndex + 1).coerceAtMost(offset + size)

        return when (offset) {
            in 0..lastIndex -> CartProducts(value.subList(offset, endIndex))
            else -> CartProducts(mutableListOf())
        }
    }
}
