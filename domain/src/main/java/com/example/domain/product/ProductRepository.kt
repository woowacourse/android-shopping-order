package com.example.domain.product

import com.example.domain.Pagination

interface ProductRepository {
    fun requestFetchProductsUnit(
        unitSize: Int,
        page: Int,
        success: (List<Product>, Pagination) -> Unit,
        failure: () -> Unit
    )

    fun requestFetchProductById(
        id: Long,
        onSuccess: (product: Product?) -> Unit,
        onFailure: () -> Unit
    )
}
