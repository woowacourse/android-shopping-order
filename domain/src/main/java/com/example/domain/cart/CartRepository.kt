package com.example.domain.cart

import com.example.domain.Pagination

interface CartRepository {

    fun requestFetchCartProductsUnit(
        unitSize: Int,
        page: Int,
        onSuccess: (List<CartProduct>, Pagination) -> Unit,
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
