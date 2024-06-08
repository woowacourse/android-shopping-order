package woowacourse.shopping.ui.payment

import woowacourse.shopping.data.coupon.CouponState

fun CouponState.toUiModel() = CouponUiModel(couponState = this)
