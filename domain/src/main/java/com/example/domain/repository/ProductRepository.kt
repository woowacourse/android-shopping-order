package com.example.domain.repository

import com.example.domain.Product

interface ProductRepository {
    fun requestFetchAllProducts(
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit
    )

    fun requestFetchProductById(
        id: Long,
        onSuccess: (product: Product?) -> Unit,
        onFailure: () -> Unit
    )
}
