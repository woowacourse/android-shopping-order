package woowacourse.shopping.ui.payment

import woowacourse.shopping.domain.model.coupon.CouponState

fun CouponState.toUiModel() = CouponUiModel(couponState = this)
