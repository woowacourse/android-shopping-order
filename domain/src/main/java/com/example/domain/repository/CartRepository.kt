package com.example.domain.repository

import com.example.domain.model.CartProduct

interface CartRepository {
    fun getAllProductInCart(callback: (List<CartProduct>) -> Unit)

    fun insert(
        id: Long,
        quantity: Int,
        callback: (Unit) -> Unit,
    )

    fun updateCount(
        id: Long,
        count: Int,
        callback: (Unit) -> Unit,
    )

    fun remove(
        id: Long,
        callback: (Unit) -> Unit,
    )

    fun findById(id: Long, callback: (CartProduct?) -> Unit)

    fun findByProductId(productId: Long, callback: (CartProduct?) -> Unit)
    fun getSubList(offset: Int, step: Int, callback: (List<CartProduct>) -> Unit)
}
