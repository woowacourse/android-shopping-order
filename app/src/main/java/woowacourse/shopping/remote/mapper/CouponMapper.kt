package woowacourse.shopping.remote.mapper

import woowacourse.shopping.data.model.remote.AvailableTimeDto
import woowacourse.shopping.data.model.remote.CouponDto
import woowacourse.shopping.remote.model.response.AvailableTimeResponse
import woowacourse.shopping.remote.model.response.CouponResponse

fun List<CouponResponse>.toData(): List<CouponDto> {
    return map { couponResponse ->
        couponResponse.toData()
    }
}

fun CouponResponse.toData(): CouponDto {
    return CouponDto(
        id = this.id,
        code = this.code,
        description = this.description,
        expirationDate = this.expirationDate,
        discount = this.discount,
        minimumAmount = this.minimumAmount,
        buyQuantity = this.buyQuantity,
        getQuantity = this.getQuantity,
        availableTime = this.availableTime?.toData(),
        discountType = this.discountType,
    )
}

fun AvailableTimeResponse.toData(): AvailableTimeDto {
    return AvailableTimeDto(
        start = this.start,
        end = this.start,
    )
}
