package woowacourse.shopping.data.model

data class DataOrderProduct(
    val productId: Long,
    val productName: String,
    val quantity: String,
    val price: Long,
    val imageUrl: String,
)
