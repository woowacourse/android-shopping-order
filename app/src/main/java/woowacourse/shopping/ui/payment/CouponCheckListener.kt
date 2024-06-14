package woowacourse.shopping.ui.payment

import woowacourse.shopping.ui.model.CouponUi

interface CouponCheckListener {
    fun onCheck(coupon: CouponUi)
}
