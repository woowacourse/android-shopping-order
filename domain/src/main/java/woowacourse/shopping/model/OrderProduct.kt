package woowacourse.shopping.model

data class OrderProduct(
    val productId: Int,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val quantity: Int,
)
