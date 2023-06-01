package woowacourse.shopping.data.model

data class OrderProductEntity(
    val productId: Long,
    val productName: String,
    val quantity: Long,
    val price: Long,
    val imageUrl: String,
)
