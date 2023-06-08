package com.shopping.repository

import com.shopping.domain.CartProduct
import com.shopping.domain.OrderProduct
import com.shopping.domain.Product

interface CartProductRepository {
    val cartProducts: List<CartProduct>
    fun getAll(onSuccess: (List<CartProduct>) -> Unit, onFailure: (Exception) -> Unit)
    fun remove(cartProduct: CartProduct)
    fun loadCartProducts(index: Pair<Int, Int>): List<CartProduct>
    fun update(cartProduct: CartProduct)
    fun add(product: Product, quantity: Int)
    fun updateCount(product: Product, count: Int, updateCartBadge: () -> Unit)
    fun getCartItemsWithIds(cartItemIds: List<Long>, onSuccess: (List<OrderProduct>) -> Unit)
}
