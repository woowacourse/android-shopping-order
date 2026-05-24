package woowacourse.shopping.domain.model.coupon

object CouponTypes {
    object FIXED5000 : CouponType
    object BOGO : CouponType
    object FREESHIPPING : CouponType
    object MIRACLESALE : CouponType
    object UNKNOWN : CouponType

    fun fromCode(code: String): CouponType = when (code) {
        "FIXED5000" -> FIXED5000
        "BOGO" -> BOGO
        "FREESHIPPING" -> FREESHIPPING
        "MIRACLESALE" -> MIRACLESALE
        else -> UNKNOWN
    }
}



