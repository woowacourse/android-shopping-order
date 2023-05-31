package com.example.domain.repository

import com.example.domain.CartProduct

interface CartRepository {

    fun getAll(
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: () -> Unit
    )

    fun addCartProduct(
        productId: Long,
        onSuccess: (cartId: Long) -> Unit,
        onFailure: () -> Unit
    )

    fun updateCartProductQuantity(
        id: Long,
        quantity: Int,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    )

    fun deleteCartProduct(
        id: Long,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    )
}
