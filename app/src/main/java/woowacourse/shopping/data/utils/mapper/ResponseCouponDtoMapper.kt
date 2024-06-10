package woowacourse.shopping.data.utils.mapper

import woowacourse.shopping.data.dto.response.ResponseCouponDto
import woowacourse.shopping.domain.model.coupon.AvailableTime
import woowacourse.shopping.domain.model.coupon.Bogo
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.CouponCode
import woowacourse.shopping.domain.model.coupon.CouponState
import woowacourse.shopping.domain.model.coupon.Fixed5000
import woowacourse.shopping.domain.model.coupon.Freeshipping
import woowacourse.shopping.domain.model.coupon.MiracleSale

fun ResponseCouponDto.toCoupon(): Coupon {
    return Coupon(
        id = this.id,
        code = CouponCode.findCode(this.code),
        description = this.description,
        expirationDate = this.expirationDate.toLocalDate(),
        discountType = this.discountType,
        discount = this.discount,
        minimumAmount = this.minimumAmount,
        buyQuantity = this.buyQuantity,
        getQuantity = this.getQuantity,
        availableTime = this.availableTime?.toModel(),
    )
}

fun ResponseCouponDto.AvailableTime.toModel() =
    AvailableTime(
        this.start.toLocalTime(),
        this.end.toLocalTime(),
    )

fun ResponseCouponDto.toDomain(): CouponState =
    when (CouponCode.findCode(this.code)) {
        CouponCode.FIXED5000 -> Fixed5000(coupon = this.toCoupon())
        CouponCode.BOGO -> Bogo(coupon = this.toCoupon())
        CouponCode.FREESHIPPING -> Freeshipping(coupon = this.toCoupon())
        CouponCode.MIRACLESALE -> MiracleSale(coupon = this.toCoupon())
    }
