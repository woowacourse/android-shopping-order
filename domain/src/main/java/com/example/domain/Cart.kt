package com.example.domain

class Cart(products: List<CartProduct> = emptyList()) {
    private val _products: MutableList<CartProduct> by lazy {
        products.distinctBy { it.productId }.toMutableList()
    }
    val products: List<CartProduct>
        get() = _products.toList()

    fun addAll(cartProducts: List<CartProduct>): Boolean = _products.addAll(cartProducts)

    fun getPickedCount(): Int = _products.count { it.isPicked }

    fun getPickedProductsTotalPrice(): Int = products
        .filter { it.isPicked }
        .sumOf { it.quantity * it.productPrice }

    fun updatePickedByIndex(cartId: Long, picked: Boolean) {
        val index = getIndexById(cartId)
        _products[index].isPicked = picked
    }

    fun updateProductQuantityByIndex(cartId: Long, quantity: Int) {
        val index = getIndexById(cartId)
        _products[index].quantity = quantity
    }

    fun isAllPicked(): Boolean = products.count() == products.count { it.isPicked }

    fun setAllPicked(picked: Boolean) = _products.map { it.isPicked = picked }

    private fun getIndexById(id: Long): Int = _products.indexOfFirst { it.id == id }
}
