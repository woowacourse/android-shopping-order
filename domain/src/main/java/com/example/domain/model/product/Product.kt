package com.example.domain.model.product

import com.example.domain.model.Price

data class Product(
    val id: Long,
    val name: String,
    val imgUrl: String,
    val price: Price
)
