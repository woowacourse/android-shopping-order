package com.example.data.datasource.remote.retrofit.model.response.product

import com.example.data.datasource.remote.retrofit.model.response.Sort
import kotlinx.serialization.Serializable

@Serializable
data class Pageable(
    val offset: Int,
    val pageNumber: Int,
    val pageSize: Int,
    val paged: Boolean,
    val sort: Sort,
    val unpaged: Boolean,
)
