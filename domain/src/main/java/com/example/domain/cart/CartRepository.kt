package com.example.domain.cart

import com.example.domain.Pagination

interface CartRepository {

    fun requestFetchCartProductsUnit(
        unitSize: Int,
        page: Int,
        success: (List<CartProduct>, Pagination) -> Unit,
        failure: () -> Unit
    )

    fun addCartProduct(
        productId: Long,
        success: (cartId: Long) -> Unit,
        failure: () -> Unit
    )

    fun updateCartProductQuantity(
        id: Long,
        quantity: Int,
        success: () -> Unit,
        failure: () -> Unit
    )

    fun deleteCartProduct(
        id: Long,
        success: () -> Unit,
        failure: () -> Unit
    )
}
