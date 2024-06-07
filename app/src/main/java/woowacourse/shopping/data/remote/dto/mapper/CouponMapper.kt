package woowacourse.shopping.data.remote.dto.mapper

import woowacourse.shopping.data.remote.dto.response.CouponResponseDto
import woowacourse.shopping.domain.Coupon

fun CouponResponseDto.toDomain(): Coupon {
    return when(this.code) {
        "FIXED5000" -> Coupon.Fixed5000(id, code, description, discountType, expirationDate, discount!!, minimumAmount!!)
        "BOGO" -> Coupon.Bogo(id, code, description, expirationDate, buyQuantity!!, getQuantity!!, discountType)
        "FREESHIPPING" -> Coupon.FreeShipping(id, code, description, expirationDate, minimumAmount!!, discountType)
        "MIRACLESALE" -> Coupon.MiracleSale(id, code, description, expirationDate, discount!!, availableTime!!.start!!, availableTime.end!!, discountType)
        else -> {Coupon.Unknown(id, code, description, expirationDate, discountType)}
    }
}
