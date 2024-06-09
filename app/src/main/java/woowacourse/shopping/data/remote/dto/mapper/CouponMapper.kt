package woowacourse.shopping.data.remote.dto.mapper

import woowacourse.shopping.data.remote.dto.response.CouponResponse
import woowacourse.shopping.domain.Coupon

fun CouponResponse.toDomain(): Coupon {
    return when (CouponType.fromCode(code)) {
        CouponType.FIXED5000 -> Coupon.Fixed5000(id, code, description, discountType, expirationDate, discount!!, minimumAmount!!)
        CouponType.BOGO -> Coupon.Bogo(id, code, description, expirationDate, buyQuantity!!, getQuantity!!, discountType)
        CouponType.FREESHIPPING -> Coupon.FreeShipping(id, code, description, expirationDate, minimumAmount!!, discountType)
        CouponType.MIRACLESALE ->
            Coupon.MiracleSale(
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
            Coupon.Unknown(id, code, description, expirationDate, discountType)
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
