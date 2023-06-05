package woowacourse.shopping.data.order.response

data class OrderDetailProductDataModel(
    val productId: Int,
    val name: String,
    val imageUrl: String,
    val price: Int,
    val quantity: Int
)
