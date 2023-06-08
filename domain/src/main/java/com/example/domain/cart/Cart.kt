package com.example.domain.cart

class Cart(products: List<CartProduct> = emptyList()) {

    private var _products: MutableList<CartProduct> = products.toMutableList()
    val products: List<CartProduct>
        get() = _products.toList()

    fun isAllPicked(): Boolean = products.count() == products.count { it.isPicked }

    fun removeById(id: Long): Boolean = _products.removeIf { it.id == id }

    fun getPickedCount(): Int = _products.count { it.isPicked }

    fun getPickedProductsTotalPrice(): Int = products
        .filter { it.isPicked }
        .sumOf { it.quantity * it.productPrice }

    fun getPickedProducts(): Cart = Cart(_products.filter { it.isPicked })

    fun getById(id: Long): CartProduct? {
        val index: Int = getIndexById(id)
        return if (index != -1) _products[index] else null
    }

    fun add(cartProduct: CartProduct) = _products.add(cartProduct)

    fun updatePickedByIndex(cartId: Long, picked: Boolean) {
        val index = getIndexById(cartId)
        _products[index].isPicked = picked
    }

    fun updateProductQuantityByIndex(cartId: Long, quantity: Int) {
        val index = getIndexById(cartId)
        _products[index].quantity = quantity
    }

    fun updateAll(cartProducts: List<CartProduct>) {
        _products = cartProducts.toMutableList()
    }

    fun setAllPicked(picked: Boolean) = _products.map { it.isPicked = picked }

    private fun getIndexById(id: Long): Int = _products.indexOfFirst { it.id == id }

    companion object {
        const val MAX_SIZE = 1_000
    }
}
