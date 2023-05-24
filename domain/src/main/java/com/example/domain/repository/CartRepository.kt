package com.example.domain.repository

import com.example.domain.CartProduct
import com.example.domain.Product

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
        id: Product,
        count: Int,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    )

    fun deleteCartProduct(
        id: Int,
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit
    )
}
