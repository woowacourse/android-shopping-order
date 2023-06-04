package com.example.domain

data class Pagination(
    val total: Int,
    val perPage: Int,
    val currentPage: Int,
    val lastPage: Int
)
