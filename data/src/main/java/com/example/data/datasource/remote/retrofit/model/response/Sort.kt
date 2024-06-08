package com.example.data.datasource.remote.retrofit.model.response

import kotlinx.serialization.Serializable

@Serializable
data class Sort(
    val empty: Boolean,
    val sorted: Boolean,
    val unsorted: Boolean,
)
