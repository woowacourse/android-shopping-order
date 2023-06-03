package woowacourse.shopping.model

data class OrderHistoryItemUIModel(
    val productId: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val quantity: Int
)
