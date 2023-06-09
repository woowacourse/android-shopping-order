package woowacourse.shopping.uimodel

data class OrderProductUIModel(
    val productId: Int,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val quantity: Int,
)
