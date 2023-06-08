package com.example.domain.repository

import com.example.domain.model.product.Product

interface ProductRepository {
    fun getProducts(
        page: Int,
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit
    )

    fun getProductById(id: Long): Product?

    fun clearCache()
}
