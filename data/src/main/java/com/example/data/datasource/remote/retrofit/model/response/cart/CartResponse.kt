package com.example.data.datasource.remote.retrofit.model.response.cart

import com.example.data.datasource.remote.retrofit.model.response.Pageable
import com.example.data.datasource.remote.retrofit.model.response.Sort
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartResponse(
    @SerialName("content")
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
