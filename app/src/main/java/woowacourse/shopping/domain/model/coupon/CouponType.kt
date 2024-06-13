package woowacourse.shopping.domain.model.coupon

enum class CouponType {
    FIXED5000,
    BOGO,
    FREESHIPPING,
    MIRACLESALE,
    ;

    companion object {
        fun matchCoupon(code: String): CouponType {
            return CouponType.entries.find { it.name == code }!!
        }
    }
}
