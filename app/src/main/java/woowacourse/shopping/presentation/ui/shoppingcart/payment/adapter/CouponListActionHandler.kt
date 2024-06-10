package woowacourse.shopping.presentation.ui.shoppingcart.payment.adapter

import woowacourse.shopping.domain.model.coupons.Coupon

interface CouponListActionHandler {
    fun selectCoupon(coupon: Coupon)
}
