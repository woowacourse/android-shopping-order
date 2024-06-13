package woowacourse.shopping.data.remote.dto.mapper

import woowacourse.shopping.data.remote.dto.mapper.CouponType.Companion.DEFAULT_BUY_QUANTITY
import woowacourse.shopping.data.remote.dto.mapper.CouponType.Companion.DEFAULT_DISCOUNT
import woowacourse.shopping.data.remote.dto.mapper.CouponType.Companion.DEFAULT_GET_QUANTITY
import woowacourse.shopping.data.remote.dto.mapper.CouponType.Companion.DEFAULT_MINIMUM_AMOUNT
import woowacourse.shopping.data.remote.dto.mapper.CouponType.Companion.DEFAULT_TIME
import woowacourse.shopping.data.remote.dto.response.CouponResponse
import woowacourse.shopping.domain.coupon.Bogo
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.Fixed
import woowacourse.shopping.domain.coupon.FreeShipping
import woowacourse.shopping.domain.coupon.MiracleSale
import woowacourse.shopping.domain.coupon.Unknown

fun CouponResponse.toDomain(): Coupon {
    return when (CouponType.fromCode(code)) {
        CouponType.FIXED5000 -> Fixed(id, code, description, discountType, expirationDate, discount ?: DEFAULT_DISCOUNT, minimumAmount ?: DEFAULT_MINIMUM_AMOUNT)
        CouponType.BOGO -> Bogo(id, code, description, expirationDate, buyQuantity ?: DEFAULT_BUY_QUANTITY, getQuantity ?: DEFAULT_GET_QUANTITY, discountType)
        CouponType.FREESHIPPING -> FreeShipping(id, code, description, expirationDate, minimumAmount ?: DEFAULT_MINIMUM_AMOUNT, discountType)
        CouponType.MIRACLESALE ->
            MiracleSale(
                id,
                code,
                description,
                expirationDate,
                discount ?: DEFAULT_DISCOUNT,
                availableTime?.start ?: DEFAULT_TIME,
                availableTime?.end ?: DEFAULT_TIME,
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
        const val DEFAULT_DISCOUNT = 0
        const val DEFAULT_MINIMUM_AMOUNT = 0
        const val DEFAULT_BUY_QUANTITY = 0
        const val DEFAULT_GET_QUANTITY = 0
        const val DEFAULT_TIME = "2024-06-13"
        fun fromCode(code: String): CouponType {
            return entries.find { couponType -> couponType.code == code } ?: UNKNOWN
        }
    }
}
