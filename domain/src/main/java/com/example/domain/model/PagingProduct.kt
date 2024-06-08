package com.example.domain.model

data class PagingProduct(
    val page: Int,
    val products: List<Product>,
    val isLast: Boolean,
)
