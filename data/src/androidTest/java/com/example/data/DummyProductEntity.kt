package com.example.data

import com.example.domain.model.RecentProduct
import java.time.LocalDateTime

val dummyRecentProducts =
    List(10) { RecentProduct(it, dummyProducts[it], LocalDateTime.of(2024, 6, 1, 1, 1, it)) }
