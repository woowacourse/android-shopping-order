package com.example.domain.repository

import com.example.domain.model.Product

interface ProductRepository {
    fun fetchFirstProducts(
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit,
    )

    fun fetchNextProducts(
        lastProductId: Long,
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit,
    )

    fun fetchProductById(
        productId: Long,
        onSuccess: (Product) -> Unit,
        onFailure: () -> Unit,
    )
}
