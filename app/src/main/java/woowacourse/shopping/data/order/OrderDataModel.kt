package woowacourse.shopping.data.order

data class OrderDataModel(
    val productId: Int,
    val name: String,
    val imageUrl: String,
    val price: Int,
    val quantity: Int
)
