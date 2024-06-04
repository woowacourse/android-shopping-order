package woowacourse.shopping.domain.model

import java.time.LocalDateTime

data class RecentProduct(
    val productId: Int,
    val productName: String,
    val imageUrl: String,
    val dateTime: LocalDateTime,
    val category: String,
)
