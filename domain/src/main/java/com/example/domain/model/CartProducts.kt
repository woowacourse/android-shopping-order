package com.example.domain.model

class CartProducts(
    data: List<CartProduct>
) {
    private val _data: MutableList<CartProduct> = data.toMutableList()
    val data get() = _data.toList()

    val size get() = _data.size

    fun delete(cartProduct: CartProduct) {
        _data.removeIf { it.cartProductId == cartProduct.cartProductId }
    }

    fun updateProductCount(cartProduct: CartProduct, count: Int) {
        val index = _data.indexOfFirst { it.cartProductId == cartProduct.cartProductId }
        _data[index] = _data[index].copy(count = count)
    }

    fun updateSelection(cartProduct: CartProduct, isSelected: Boolean) {
        val index = _data.indexOfFirst { it.cartProductId == cartProduct.cartProductId }
        _data[index] = _data[index].copy(isSelected = isSelected)
    }

    fun cartProductsByRange(fromIndex: Int, toIndex: Int): List<CartProduct> {
        return _data.subList(fromIndex, toIndex)
    }

    fun selectedProducts(): List<CartProduct> {
        return _data.filter { it.isSelected }
    }

    fun findByProductId(productId: Long): CartProduct? {
        return _data.find { it.product.id == productId }
    }
}
