package woowacourse.shopping.mapper

import woowacourse.shopping.data.model.CouponResponse
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.Coupon.AvailableTime

fun CouponResponse.toDomain() =
    Coupon(
        id = this.id,
        code = this.code,
        description = this.description,
        expirationDate = this.expirationDate,
        discountType = this.discountType,
        discount = this.discount,
        buyQuantity = this.buyQuantity,
        getQuantity = this.getQuantity,
        minimumAmount = this.minimumAmount,
        availableTime =
            this.availableTime?.let {
                AvailableTime(
                    start = it.start,
                    end = it.end,
                )
            },
    )
