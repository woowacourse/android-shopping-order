package woowacourse.shopping.model

class CartProducts(cartProducts: List<CartProduct>) {
    private val cartProducts: MutableList<CartProduct> = cartProducts.toMutableList()
    val size: Int get() = cartProducts.size

    private val checkedProductsIds = mutableSetOf<Int>()
    val checkedIds: List<Int>
        get() = checkedProductsIds.toList()

    val totalPrice: Int
        get() = cartProducts.sumOf { it.product.price * it.quantity }

    val totalQuantity: Int
        get() = cartProducts.sumOf { it.quantity }

    val totalCheckedPrice: Int
        get() = cartProducts.filter { checkedProductsIds.contains(it.id) }
            .sumOf { it.product.price * it.quantity }

    val totalCheckedQuantity: Int
        get() = cartProducts.filter { checkedProductsIds.contains(it.id) }.sumOf { it.quantity }

    init {
        this.checkedProductsIds.addAll(cartProducts.map { it.id })
    }

    operator fun get(index: Int): CartProduct {
        return cartProducts[index]
    }

    fun all(): List<CartProduct> {
        return cartProducts.toList()
    }

    fun changeChecked(id: Int, checked: Boolean) {
        val index = cartProducts.indexOfFirst { it.id == id }

        if (checked) {
            checkedProductsIds.add(cartProducts[index].id)
        } else {
            checkedProductsIds.remove(cartProducts[index].id)
        }
    }

    fun findByProductId(productId: Int): CartProduct? {
        return cartProducts.firstOrNull { it.product.id == productId }
    }

    fun replaceAll(cartProducts: List<CartProduct>) {
        this.cartProducts.clear()
        this.cartProducts.addAll(cartProducts)
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

    fun getCheckedState(item: CartProduct): Boolean {
        return checkedProductsIds.contains(item.id)
    }
}
