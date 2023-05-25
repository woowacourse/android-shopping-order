package woowacourse.shopping.model

class CartItemsUIModel(
    cartProducts: List<CartProductUIModel> = mutableListOf(),
) {
    private val _cartProducts = cartProducts.toMutableList()

    override fun toString(): String {
        return _cartProducts.toString()
    }

    fun updateItems(data: List<CartProductUIModel>) {
        _cartProducts.clear()
        _cartProducts.addAll(data)
    }

    fun insert(cartProduct: CartProductUIModel) {
        if (!_cartProducts.contains(cartProduct)) {
            _cartProducts.add(cartProduct)
        }
    }

    fun remove(id: Long) {
        _cartProducts.removeIf { it.product.id == id }
    }

    fun getPrice(): Int {
        return _cartProducts.sumOf { it.product.price * it.count }
    }

    fun isContain(id: Long): Boolean {
        return _cartProducts.any { it.product.id == id }
    }

    fun getSize(): Int {
        return _cartProducts.size
    }
}
