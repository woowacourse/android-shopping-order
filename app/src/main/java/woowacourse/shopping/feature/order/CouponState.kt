package woowacourse.shopping.feature.order

import woowacourse.shopping.domain.model.Coupon

data class CouponState(
    val item: Coupon,
    val checked: Boolean = false,
)
