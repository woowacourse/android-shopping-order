package woowacourse.shopping.domain.model

enum class CouponCodeType {
    FIXED5000,
    BOGO,
    FREESHIPPING,
    MIRACLESALE,
    ;

    companion object {
        fun from(code: String): CouponCodeType? = entries.find { it.name.equals(code, ignoreCase = true) }
    }
}
