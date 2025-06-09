package woowacourse.shopping.view.receipt


data class CouponItem(
    val couponId: Long,
    val description: String,
    val expirationDate: String,
    val minimumOrderPrice: Int? = null,
    val availableTime: String? = null
)