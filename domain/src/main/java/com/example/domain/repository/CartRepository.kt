package com.example.domain.repository

import com.example.domain.model.CartProduct

interface CartRepository {
    fun getAll(
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: () -> Unit,
    )

    fun getSize(
        onSuccess: (cartCount: Int) -> Unit,
        onFailure: () -> Unit
    )

    fun addCartProduct(
        productId: Long,
        onSuccess: (cartId: Long) -> Unit,
        onFailure: () -> Unit,
    )

    fun changeCartProductCount(
        cartId: Long,
        count: Int,
        onSuccess: (cartId: Long) -> Unit,
        onFailure: () -> Unit,
    )

    fun deleteCartProduct(
        cartId: Long,
        onSuccess: (cartId: Long) -> Unit,
        onFailure: () -> Unit,
    )
}
