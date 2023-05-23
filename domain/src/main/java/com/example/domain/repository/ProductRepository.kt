package com.example.domain.repository

import com.example.domain.Product

interface ProductRepository {
    fun getAll(
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit
    )

    fun getProduct(
        id: Int,
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit
    )

    fun addProduct(
        name: String,
        price: Int,
        imageUrl: String,
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit
    )

    fun updateProduct(
        product: Product,
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit
    )

    fun deleteProduct(
        id: Int,
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit
    )
}
