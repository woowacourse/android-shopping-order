package com.example.domain.datasource

import com.example.domain.model.CartItem
import com.example.domain.model.Quantity

interface CartDataSource {
    fun findAll(): DataResponse<List<CartItem>>

    fun increaseQuantity(productId: Int)

    fun decreaseQuantity(productId: Int)

    fun changeQuantity(
        cartItemId: Int,
        quantity: Quantity,
    )

    fun deleteByProductIdCartItem(productId: Int)

    fun findByProductId(productId: Int): DataResponse<CartItem>

    fun findRange(
        page: Int,
        pageSize: Int,
    ): DataResponse<List<CartItem>>

    fun totalCartItemCount(): DataResponse<Int>
}
