package com.example.domain.model

import java.time.LocalDateTime

class RecentProduct(
    var id: Int = 0,
    val product: Product,
    val seenDateTime: LocalDateTime,
)
