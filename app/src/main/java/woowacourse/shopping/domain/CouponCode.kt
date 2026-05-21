package woowacourse.shopping.domain

enum class CouponCode {
    FIXED5000,
    BOGO,
    FREESHIPPING,
    MIRACLESALE;

    companion object {
        fun toCodeString(couponCode: CouponCode): String {
            return when (couponCode) {
                FIXED5000 -> "FIXED5000"
                BOGO -> "BOGO"
                FREESHIPPING -> "FREESHIPPING"
                MIRACLESALE -> "MIRACLESALE"
            }
        }

        fun fromCodeString(code: String): CouponCode {
            return when (code) {
                "FIXED5000" -> FIXED5000
                "BOGO" -> BOGO
                "FREESHIPPING" -> FREESHIPPING
                "MIRACLESALE" -> MIRACLESALE
                else -> throw IllegalArgumentException("잘못된 쿠폰 코드입니다.")
            }
        }
    }
}
