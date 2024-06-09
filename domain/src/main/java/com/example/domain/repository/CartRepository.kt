package com.example.domain.repository

import com.example.domain.datasource.DataResponse
import com.example.domain.model.CartItem
import com.example.domain.model.Quantity

interface CartRepository {
    suspend fun findAll(): DataResponse<List<CartItem>>

    suspend fun postCartItem(
        productId: Int,
        quantity: Quantity,
    ): DataResponse<Unit>

    suspend fun increaseQuantity(productId: Int): DataResponse<Unit>

    suspend fun decreaseQuantity(productId: Int): DataResponse<Unit>

    suspend fun changeQuantity(
        productId: Int,
        quantity: Quantity,
    ): DataResponse<Unit>

    suspend fun deleteCartItem(cartItemId: Int): DataResponse<Unit>

    suspend fun find(productId: Int): DataResponse<CartItem>

    suspend fun findRange(
        page: Int,
        pageSize: Int,
    ): DataResponse<List<CartItem>>

    suspend fun totalCartItemCount(): DataResponse<Int>
}
