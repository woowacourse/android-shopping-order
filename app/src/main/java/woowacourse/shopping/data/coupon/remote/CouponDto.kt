package woowacourse.shopping.data.coupon.remote

class CouponDto(
    val id: Int,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discount: Int?,
    val minimumAmount: Int?,
    val buyQuantity: Int?,
    val getQuantity: Int?,
    val availableTime: AvailableTimeDto?,
    val discountType: String,
)
