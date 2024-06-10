package com.example.data.datasource.remote.model.response.product

import com.example.data.datasource.remote.model.response.Pageable
import com.example.data.datasource.remote.model.response.Sort
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    @SerialName("content")
    val productContent: List<ProductContent>,
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
