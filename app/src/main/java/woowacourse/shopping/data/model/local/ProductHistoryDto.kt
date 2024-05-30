package woowacourse.shopping.data.model.local

import java.time.LocalDateTime

data class ProductHistoryDto(
    val productId: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String,
    val createAt: LocalDateTime,
)
