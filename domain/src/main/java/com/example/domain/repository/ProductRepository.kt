package com.example.domain.repository

import com.example.domain.Pagination
import com.example.domain.Product

interface ProductRepository {
    fun requestFetchProductsUnit(
        unitSize: Int,
        page: Int,
        onSuccess: (List<Product>, Pagination) -> Unit,
        onFailure: () -> Unit
    )

    fun requestFetchProductById(
        id: Long,
        onSuccess: (product: Product?) -> Unit,
        onFailure: () -> Unit
    )
}
