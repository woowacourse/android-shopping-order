package woowacourse.shopping.mapper

import woowacourse.shopping.data.model.Coupon
import woowacourse.shopping.data.model.Coupon.AvailableTime
import woowacourse.shopping.data.model.CouponResponse

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
