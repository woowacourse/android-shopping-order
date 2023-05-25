package com.example.domain.repository

import com.example.domain.model.CartProduct

interface CartRepository {
    fun getAllProductInCart(
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    fun insert(
        cartProduct: CartProduct,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    fun remove(
        id: Long,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    fun updateCount(
        id: Long,
        count: Int,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    fun findById(id: Long): CartProduct?
    fun getSubList(offset: Int, step: Int): List<CartProduct>
}
