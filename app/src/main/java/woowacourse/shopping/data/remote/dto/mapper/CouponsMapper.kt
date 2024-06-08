package woowacourse.shopping.data.remote.dto.mapper

import woowacourse.shopping.data.remote.dto.response.Coupons
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.CouponType

fun Coupons.toDomain(): Coupon {
    return Coupon(
        id = this.id,
        code = CouponType.valueOf(this.code).name,
        expirationDate = this.expirationDate,
        discountType = this.discountType,
        description = this.description,
        discount = this.discount,
        minimumAmount = this.minimumAmount,
        buyQuantity = this.buyQuantity,
        getQuantity = this.getQuantity,
        availableTime = this.availableTime?.toDomain(),
    )
}
