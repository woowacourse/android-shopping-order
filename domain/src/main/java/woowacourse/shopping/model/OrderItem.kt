package woowacourse.shopping.model

data class OrderItem(
    val productId: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val quantity: Int
)
