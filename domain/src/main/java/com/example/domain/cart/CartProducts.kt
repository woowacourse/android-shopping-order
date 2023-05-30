package com.example.domain.cart

class CartProducts(private val cartProducts: List<CartProduct>) {

    val totalCheckedPrice: Int
        get() {
            return cartProducts.filter { it.checked }.sumOf { cartProduct ->
                cartProduct.count * cartProduct.product.price
            }
        }

    val size: Int
        get() = cartProducts.size

    val all: List<CartProduct>
        get() = cartProducts.map { it.copy() }

    fun getOrNull(cartId: Long): CartProduct? {
        return cartProducts.find { it.id == cartId }
    }

    fun remove(cartId: Long): CartProducts {
        return CartProducts(all.filterNot { it.id == cartId }.toMutableList())
    }

    fun updateCheckedBy(cartId: Long, isChecked: Boolean): CartProducts {
        val newAll = all
        val cartProduct = newAll.find { it.id == cartId } ?: return this
        cartProduct.updateChecked(isChecked)
        return CartProducts(newAll)
    }

    fun updateAllCheckedBy(cartIds: List<Long>, isChecked: Boolean): CartProducts {
        val newAll = all.map {
            if (cartIds.contains(it.id)) {
                it.updateChecked(isChecked)
            } else {
                it
            }
        }
        return CartProducts(newAll)
    }

    fun updateCheckedAll(isChecked: Boolean): CartProducts {
        val newAll = all.map { it.updateChecked(isChecked) }
        println(newAll)
        return CartProducts(newAll)
    }

    fun changeCount(cartId: Long, count: Int): CartProducts {
        if (count < 1) return this
        val newAll = all.toMutableList()
        val cartProduct = newAll.find { it.id == cartId } ?: return this
        val index = newAll.indexOf(cartProduct)
        val newProduct = cartProduct.updateCount(count)
        newAll.remove(cartProduct)
        newAll.add(index, newProduct)
        return CartProducts(newAll)
    }
}
