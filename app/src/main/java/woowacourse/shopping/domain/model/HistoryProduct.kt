package woowacourse.shopping.domain.model

data class HistoryProduct(
    val productId: Long,
    val name: String,
    val imageUrl: String,
    val category: String,
)
