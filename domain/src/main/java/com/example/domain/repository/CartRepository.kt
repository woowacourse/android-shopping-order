package com.example.domain.repository

import com.example.domain.model.CartProduct
import com.example.domain.model.CustomError

interface CartRepository {
    fun getAll(
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: (CustomError) -> Unit,
    )

    fun addCartProduct(
        productId: Long,
        onSuccess: (cartId: Long) -> Unit,
        onFailure: (CustomError) -> Unit,
    )

    fun changeCartProductCount(
        cartId: Long,
        count: Int,
        onSuccess: (cartId: Long) -> Unit,
        onFailure: (CustomError) -> Unit,
    )

    fun deleteCartProduct(
        cartId: Long,
        onSuccess: (cartId: Long) -> Unit,
        onFailure: (CustomError) -> Unit,
    )

    fun getSize(onSuccess: (cartCount: Int) -> Unit, onFailure: (CustomError) -> Unit)
}
