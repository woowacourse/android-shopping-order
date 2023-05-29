package woowacourse.shopping.ui.model

data class OrderProduct(
    val productId: Long,
    val productName: String,
    val quantity: String,
    val price: Long,
    val imageUrl: String,
)
