package com.example.domain.repository

import com.example.domain.CartProduct

interface CartRepository {

    fun getAll(
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: () -> Unit
    )

    fun addCartProduct(
        productId: Int,
        onSuccess: (cartId: Int) -> Unit,
        onFailure: () -> Unit
    )

    fun updateCartProductQuantity(
        id: Int,
        quantity: Int,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    )

    fun deleteCartProduct(
        id: Int,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    )
}
