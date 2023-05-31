package com.example.domain.repository

import com.example.domain.model.CartProduct
import com.example.domain.model.CartProducts
import com.example.domain.model.Product

interface CartRepository {
    fun getAll(): CartProducts
    fun addProduct(product: Product): Int
    fun updateProduct(cartItemId: Int, count: Int)
    fun deleteProduct(cartItemId: Int)
    fun getCartProductByProduct(product: Product): CartProduct?
}
