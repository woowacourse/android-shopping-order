package woowacourse.shopping.view.order.state

import woowacourse.shopping.domain.coupon.Coupon

data class CouponState(
    val item: Coupon,
    val checked: Boolean = false,
) {
    fun toggleChecked() = copy(checked = !checked)
}
