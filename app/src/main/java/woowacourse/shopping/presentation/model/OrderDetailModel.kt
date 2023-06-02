package woowacourse.shopping.presentation.model

data class OrderDetailModel(
    val id: Long,
    val usedPoint: Int,
    val savedPoint: Int,
    val orderDateTime: String,
    val products: List<CartModel>,
)
