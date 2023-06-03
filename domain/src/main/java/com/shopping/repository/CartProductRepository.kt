package com.shopping.repository

import com.shopping.domain.CartProduct
import com.shopping.domain.Product

interface CartProductRepository {
    fun getAll(onSuccess: (List<CartProduct>) -> Unit, onFailure: (Exception) -> Unit)
    fun getAllProductsCount(): Int
    fun remove(cartProduct: CartProduct)
    fun loadCartProducts(index: Pair<Int, Int>): List<CartProduct>
    fun update(cartProduct: CartProduct)
    fun add(cartProduct: CartProduct)
    fun findCountById(id: Int): Int
    fun updateCount(product: Product, count: Int)
}
