package woowacourse.shopping.data.remote.dto.mapper

import woowacourse.shopping.data.remote.dto.response.CouponResponseDto
import woowacourse.shopping.domain.Coupon


fun CouponResponseDto.toDomain(): Coupon {
    return Coupon(
        id = id,
        code = code,
        description = description,
        expirationDate = expirationDate,
        discount = discount,
        buyQuantity = buyQuantity,
        getQuantity = getQuantity,
        minimumAmount = minimumAmount,
        availableTimeStart = availableTime?.start,
        availableTimeEnd = availableTime?.end,
        discountType = discountType
    )
}