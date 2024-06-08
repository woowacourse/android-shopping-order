package com.example.data.datasource.remote.retrofit.model.response.product

import kotlinx.serialization.Serializable

@Serializable
data class ProductContent(
    val id: Int,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String,
)
