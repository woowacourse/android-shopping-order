package woowacourse.shopping.domain.model

import woowacourse.shopping.utils.exception.ErrorEvent

enum class Coupon {
    FIXED5000,
    BOGO,
    FREESHIPPING,
    MIRACLESALE,
    ;

    companion object {
        fun matchCoupon(code: String): Coupon {
            return Coupon.entries.find { it.name == code } ?: throw ErrorEvent.LoadCouponEvent()
        }
    }
}
