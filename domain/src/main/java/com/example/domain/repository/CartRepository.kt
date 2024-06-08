package com.example.domain.repository

import com.example.domain.model.CartItem
import com.example.domain.model.Quantity

interface CartRepository {
    fun findAll(): List<CartItem>

    fun increaseQuantity(productId: Int)

    fun decreaseQuantity(productId: Int)

    fun changeQuantity(
        productId: Int,
        quantity: Quantity,
    )

    fun deleteCartItem(productId: Int)

    fun find(productId: Int): CartItem

    fun findRange(
        page: Int,
        pageSize: Int,
    ): List<CartItem>

    fun totalCartItemCount(): Int
}
