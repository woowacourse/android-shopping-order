package woowacourse.shopping.presentation.model

data class OrderDetailProductModel(
    val productId: Int,
    val name: String,
    val imageUrl: String,
    val price: Int,
    val quantity: Int
)
