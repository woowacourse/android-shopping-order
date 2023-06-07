package com.example.domain.repository

import com.example.domain.model.FailureInfo
import com.example.domain.model.Product

interface ProductRepository {
    fun getProducts(
        page: Int,
        onSuccess: (List<Product>) -> Unit,
        onFailure: (FailureInfo) -> Unit
    )

    fun getProductById(id: Long): Product?

    fun clearCache()
}
