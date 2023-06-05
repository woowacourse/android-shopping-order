package com.shopping.repository

import com.shopping.domain.CartProduct
import com.shopping.domain.Product

interface CartProductRepository {
    fun getAll(onSuccess: (List<CartProduct>) -> Unit, onFailure: (Exception) -> Unit)
    fun getAllProductsCount(): Int
    fun remove(cartProduct: CartProduct)
    fun loadCartProducts(index: Pair<Int, Int>): List<CartProduct>
    fun update(cartProduct: CartProduct)
    fun add(product: Product)
    fun findCountById(id: Long): Int
    fun updateCount(product: Product, count: Int, updateCartBadge: () -> Unit)
    fun getTotalPrice(): Int
    fun getTotalCount(): Int
}