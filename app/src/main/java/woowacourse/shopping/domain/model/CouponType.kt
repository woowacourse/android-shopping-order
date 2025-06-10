package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.policy.CouponPolicy

enum class CouponType(
    val code: String,
) {
    FIXED5000("FIXED5000"),
    FREESHIPPING("FREESHIPPING"),
    MIRACLESALE("MIRACLESALE"),
    BOGO("BOGO"),
    DEFAULT("DEFAULT"),
    ;

    companion object {
        fun from(code: String): CouponType = entries.find { it.code == code.trim() } ?: DEFAULT
    }

    fun getPolicy(): CouponPolicy =
        when (this) {
            FIXED5000 -> CouponPolicy.Fixed5000
            FREESHIPPING -> CouponPolicy.FreeShipping
            MIRACLESALE -> CouponPolicy.MiracleSale
            BOGO -> CouponPolicy.Bogo
            DEFAULT -> CouponPolicy.Default
        }
}
