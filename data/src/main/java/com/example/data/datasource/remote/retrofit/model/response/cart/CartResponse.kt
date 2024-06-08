package com.example.data.datasource.remote.retrofit.model.response.cart

import com.example.data.datasource.remote.retrofit.model.response.Sort
import com.example.data.datasource.remote.retrofit.model.response.product.Pageable
import kotlinx.serialization.Serializable

@Serializable
data class CartResponse(
    val cartContent: List<CartContent>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: Pageable,
    val size: Int,
    val sort: Sort,
    val totalElements: Int,
    val totalPages: Int,
)
