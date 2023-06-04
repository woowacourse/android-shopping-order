package woowacourse.shopping.model

data class OrderUIModel(
    val id: Long,
    val orderProducts: List<OrderProductUIModel>,
    val timestamp: String,
    val couponName: String?,
    val originPrice: Int,
    val discountPrice: Int,
    val totalPrice: Int,
)
