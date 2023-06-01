package woowacourse.shopping.model

import java.time.LocalDateTime

data class OrderMinInfoItemUiModel(
    val id: Long,
    val mainProductName: String,
    val mainProductImage: String,
    val extraProductCount: Int,
    val date: LocalDateTime,
    val price: Int
)
