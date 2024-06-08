package com.example.domain.repository

import com.example.domain.model.Product

interface ProductRepository {
    fun find(id: Int): Product

    fun findRange(
        page: Int,
        pageSize: Int,
    ): List<Product>
}
