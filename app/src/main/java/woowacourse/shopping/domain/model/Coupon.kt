package woowacourse.shopping.domain.model

data class Coupon(
    val couponDetail: CouponDetail,
    val isApplied: Boolean = false,
)
