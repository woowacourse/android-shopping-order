package woowacourse.shopping.data.remote.dto.mapper

import woowacourse.shopping.data.remote.dto.response.CouponResponse
import woowacourse.shopping.domain.coupon.Bogo
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.Fixed
import woowacourse.shopping.domain.coupon.FreeShipping
import woowacourse.shopping.domain.coupon.MiracleSale
import woowacourse.shopping.domain.coupon.Unknown

fun CouponResponse.toDomain(): Coupon {
    return when (CouponType.fromCode(code)) {
        CouponType.FIXED5000 -> Fixed(id, code, description, discountType, expirationDate, discount!!, minimumAmount!!)
        CouponType.BOGO -> Bogo(id, code, description, expirationDate, buyQuantity!!, getQuantity!!, discountType)
        CouponType.FREESHIPPING -> FreeShipping(id, code, description, expirationDate, minimumAmount!!, discountType)
        CouponType.MIRACLESALE ->
            MiracleSale(
                id,
                code,
                description,
                expirationDate,
                discount!!,
                availableTime!!.start!!,
                availableTime.end!!,
                discountType,
            )
        CouponType.UNKNOWN -> {
            Unknown(id, code, description, expirationDate, discountType)
        }
    }
}

enum class CouponType(val code: String) {
    FIXED5000("FIXED5000"),
    BOGO("BOGO"),
    FREESHIPPING("FREESHIPPING"),
    MIRACLESALE("MIRACLESALE"),
    UNKNOWN("UNKNOWN"),
    ;

    companion object {
        fun fromCode(code: String): CouponType {
            return entries.find { couponType -> couponType.code == code } ?: UNKNOWN
        }
    }
}
