package woowacourse.shopping.view.order

import woowacourse.shopping.domain.coupon.Coupon

data class CouponState(
    val coupon: Coupon,
    val isSelected: Boolean,
)
