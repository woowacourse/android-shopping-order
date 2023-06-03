package woowacourse.shopping.model

class CartProducts(private val value: MutableList<CartProduct>) : MutableList<CartProduct> by value {
    private val checks = value.associateBy({ it.id }, { true }).toMutableMap()

    val checkedProducts: List<CartProduct>
        get() = value.filter { checks.getOrPut(it.id) { true } }

    val totalPrice: Int
        get() = checkedProducts.sumOf { it.product.price * it.quantity }

    val totalCheckedQuantity: Int
        get() = checkedProducts.sumOf { it.quantity }

    val totalQuantity: Int
        get() = value.sumOf { it.quantity }

    fun setCheck(id: Int, checked: Boolean) {
        checks[id] = checked
    }

    fun getCheck(id: Int): Boolean {
        return checks.getOrDefault(id, false)
    }

    fun replaceAll(cartProducts: List<CartProduct>) {
        this.value.clear()
        this.value.addAll(cartProducts)
        cartProducts.forEach { checks.putIfAbsent(it.id, true) }
    }

    override fun subList(fromIndex: Int, toIndex: Int): CartProducts {
        val lastIndex = value.lastIndex
        val endIndex = (lastIndex + 1).coerceAtMost(fromIndex + toIndex)

        return when (fromIndex) {
            in 0..lastIndex -> CartProducts(value.subList(fromIndex, endIndex))
            else -> CartProducts(mutableListOf())
        }
    }
}
