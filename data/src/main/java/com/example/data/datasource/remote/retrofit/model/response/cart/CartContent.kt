package com.example.data.datasource.remote.retrofit.model.response.cart

import com.example.data.datasource.remote.retrofit.model.response.product.ProductContent
import kotlinx.serialization.Serializable

@Serializable
data class CartContent(
    val id: Int,
    val product: ProductContent,
    val quantity: Int,
)
