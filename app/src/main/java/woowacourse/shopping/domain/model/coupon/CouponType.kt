package woowacourse.shopping.domain.model.coupon

enum class CouponType(
    val apiCode: String,
) {
    FIXED5000("fixed"),
    BOGO("buyxgety"),
    FREESHIPPING("freeshipping"),
    MIRACLESALE("percentage"),
    UNKNOWN(""),
    ;

    companion object {
        fun fromApiCode(rawCode: String): CouponType {
            val normalized = rawCode.trim().lowercase()
            return entries.firstOrNull { it.apiCode == normalized } ?: UNKNOWN
        }
    }
}
