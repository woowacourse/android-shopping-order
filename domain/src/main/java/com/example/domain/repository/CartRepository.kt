package com.example.domain.repository

import com.example.domain.model.CartProduct

interface CartRepository {
    fun getAllProductInCart(): Result<List<CartProduct>>

    fun insert(
        id: Long,
    ): Result<Unit>

    fun updateCount(
        id: Long,
        count: Int,
    ): Result<Unit>

    fun remove(
        id: Long,
    ): Result<Unit>

    fun findById(id: Long): Result<CartProduct>
    fun getSubList(offset: Int, step: Int): Result<List<CartProduct>>
}
