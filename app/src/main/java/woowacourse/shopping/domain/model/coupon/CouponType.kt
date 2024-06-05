package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.utils.exception.ErrorEvent

enum class CouponType {
    FIXED5000,
    BOGO,
    FREESHIPPING,
    MIRACLESALE,
    ;

    companion object {
        fun matchCoupon(code: String): CouponType {
            return CouponType.entries.find { it.name == code } ?: throw ErrorEvent.LoadCouponEvent()
        }
    }
}
