package com.example.domain.repository

import com.example.domain.model.Product

interface ProductRepository {
    fun getAllProducts(onSuccess: (List<Product>) -> Unit, onFailure: (Exception) -> Unit)
    fun getMoreProducts(unit: Int, previousSize: Int): List<Product>
    fun getAll(onSuccess: (List<Product>) -> Unit, onFailure: (Exception) -> Unit)
    fun getNext(count: Int, onSuccess: (List<Product>) -> Unit, onFailure: (Exception) -> Unit)
    fun findById(id: Long, onSuccess: (Product) -> Unit, onFailure: (Exception) -> Unit)
    fun findProductById(id: Long)
}
