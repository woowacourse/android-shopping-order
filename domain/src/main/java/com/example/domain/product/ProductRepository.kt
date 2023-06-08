package com.example.domain.product

import com.example.domain.Pagination

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
