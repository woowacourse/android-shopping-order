package com.example.domain.repository

import com.example.domain.model.CartProduct

interface CartRepository {
    fun getAllProductInCart(
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: (Exception) -> Unit,
    )

    fun insert(
        id: Long,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    )

    fun updateCount(
        id: Long,
        count: Int,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    )

    fun remove(
        id: Long,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    )

    fun findById(id: Long, onSuccess: (CartProduct?) -> Unit)
    fun getSubList(offset: Int, step: Int, onSuccess: (List<CartProduct>) -> Unit)
}
