package com.example.domain.repository

import com.example.domain.model.CartProduct

interface CartRepository {
    fun fetchAll(
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: () -> Unit,
    )

    fun fetchSize(
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
