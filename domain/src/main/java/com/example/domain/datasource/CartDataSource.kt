package com.example.domain.datasource

import com.example.domain.model.CartItem
import com.example.domain.model.Quantity

interface CartDataSource {
    fun findAll(): DataResponse<List<CartItem>>

    fun postCartItem(
        productId: Int,
        quantity: Quantity,
    ): DataResponse<Unit>

    fun increaseQuantity(productId: Int): DataResponse<Unit>

    fun decreaseQuantity(productId: Int): DataResponse<Unit>

    fun changeQuantity(
        cartItemId: Int,
        quantity: Quantity,
    ): DataResponse<Unit>

    fun deleteCartItem(cartItemId: Int): DataResponse<Unit>

    fun findByProductId(productId: Int): DataResponse<CartItem>

    fun findRange(
        page: Int,
        pageSize: Int,
    ): DataResponse<List<CartItem>>

    fun totalCartItemCount(): DataResponse<Int>
}
