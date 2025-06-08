package woowacourse.shopping.data.model

import woowacourse.shopping.domain.repository.CouponPolicy

enum class CouponType(val code: String) {
    FIXED5000("FIXED5000"),
    FREESHIPPING("FREESHIPPING"),
    MIRACLESALE("MIRACLESALE"),
    BOGO("BOGO"),
    DEFAULT("DEFAULT");

    companion object {
        fun from(code: String): CouponType {
            return entries.find { it.code == code.trim() } ?: DEFAULT
        }
    }

    fun getPolicy(): CouponPolicy {
        return when (this) {
            FIXED5000 -> CouponPolicy.Fixed5000
            FREESHIPPING -> CouponPolicy.FreeShipping
            MIRACLESALE -> CouponPolicy.MiracleSale
            BOGO -> CouponPolicy.Bogo
            DEFAULT -> CouponPolicy.Default
        }
    }
}