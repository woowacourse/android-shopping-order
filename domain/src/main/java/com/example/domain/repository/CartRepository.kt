package com.example.domain.repository

import com.example.domain.datasource.DataResponse
import com.example.domain.model.CartItem
import com.example.domain.model.Quantity

interface CartRepository {
    fun findAll(): DataResponse<List<CartItem>>

    fun increaseQuantity(productId: Int)

    fun decreaseQuantity(productId: Int)

    fun changeQuantity(
        productId: Int,
        quantity: Quantity,
    )

    fun deleteCartItem(productId: Int)

    fun find(productId: Int): DataResponse<CartItem>

    fun findRange(
        page: Int,
        pageSize: Int,
    ): DataResponse<List<CartItem>>

    fun totalCartItemCount(): DataResponse<Int>
}
