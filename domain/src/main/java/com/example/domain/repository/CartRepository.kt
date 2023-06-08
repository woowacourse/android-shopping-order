package com.example.domain.repository

import com.example.domain.model.CartProduct
import com.example.domain.model.CartProducts
import com.example.domain.model.FailureInfo
import com.example.domain.model.Product

interface CartRepository {
    fun getAll(
        onSuccess: (CartProducts) -> Unit,
        onFailure: (FailureInfo) -> Unit
    )

    fun addProduct(
        product: Product,
        onSuccess: (cartItemId: Int) -> Unit,
        onFailure: (FailureInfo) -> Unit
    )

    fun updateProduct(
        cartItemId: Int,
        count: Int,
        onSuccess: () -> Unit,
        onFailure: (FailureInfo) -> Unit
    )

    fun deleteProduct(
        cartItemId: Int,
        onSuccess: () -> Unit,
        onFailure: (FailureInfo) -> Unit
    )

    fun getCartProductByProduct(
        product: Product,
        onSuccess: (CartProduct?) -> Unit,
        onFailure: (FailureInfo) -> Unit
    )
}
