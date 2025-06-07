package woowacourse.shopping.view.payment.adapter

import woowacourse.shopping.domain.model.coupon.Coupon

data class CouponItem(
    val coupon: Coupon,
    val isSelected: Boolean = false,
)
