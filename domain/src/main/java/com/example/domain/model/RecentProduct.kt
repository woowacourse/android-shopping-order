package com.example.domain.model

class RecentProduct(
    var id: Int = 0,
    val product: Product,
    val seenDateTime: Long,
)
