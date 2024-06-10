package woowacourse.shopping.remote.mapper

import woowacourse.shopping.data.model.remote.AvailableTimeDto
import woowacourse.shopping.data.model.remote.CouponCodeDto
import woowacourse.shopping.data.model.remote.CouponDto
import woowacourse.shopping.data.model.remote.DiscountTypeDto
import woowacourse.shopping.remote.model.response.AvailableTimeResponse
import woowacourse.shopping.remote.model.response.CouponCodeResponse
import woowacourse.shopping.remote.model.response.CouponResponse
import woowacourse.shopping.remote.model.response.DiscountTypeResponse

fun List<CouponResponse>.toData(): List<CouponDto> {
    return map { couponResponse ->
        couponResponse.toData()
    }
}

fun CouponResponse.toData(): CouponDto {
    return CouponDto(
        id = this.id,
        code = this.code.toData(),
        description = this.description,
        expirationDate = this.expirationDate,
        discount = this.discount,
        minimumAmount = this.minimumAmount,
        buyQuantity = this.buyQuantity,
        getQuantity = this.getQuantity,
        availableTime = this.availableTime?.toData(),
        discountType = this.discountType.toData(),
    )
}

fun CouponCodeResponse.toData(): CouponCodeDto {
    return when (this) {
        CouponCodeResponse.FIXED5000 -> CouponCodeDto.FIXED5000
        CouponCodeResponse.BOGO -> CouponCodeDto.BOGO
        CouponCodeResponse.FREESHIPPING -> CouponCodeDto.FREESHIPPING
        CouponCodeResponse.MIRACLESALE -> CouponCodeDto.MIRACLESALE
    }
}

fun DiscountTypeResponse.toData(): DiscountTypeDto {
    return when (this) {
        DiscountTypeResponse.FIXED -> DiscountTypeDto.FIXED
        DiscountTypeResponse.BUY_X_GET_Y -> DiscountTypeDto.BUY_X_GET_Y
        DiscountTypeResponse.FREE_SHIPPING -> DiscountTypeDto.FREE_SHIPPING
        DiscountTypeResponse.PERCENTAGE -> DiscountTypeDto.PERCENTAGE
    }
}

fun AvailableTimeResponse.toData(): AvailableTimeDto {
    return AvailableTimeDto(
        start = this.start,
        end = this.start,
    )
}
