package woowacourse.shopping.utils

import woowacourse.shopping.data.remote.dto.coupon.AvailableTimeDto
import woowacourse.shopping.data.remote.dto.coupon.CouponDto
import woowacourse.shopping.domain.model.AvailableTime
import woowacourse.shopping.domain.model.ItemSelector
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.CouponType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object CouponMapper {
    fun CouponDto.toCoupon(): Coupon {
        return Coupon(
            id = id,
            expirationDate = this.formatExpirationDate(),
            couponType = CouponType.matchCoupon(code),
            description = description,
            discountType = discountType,
            discount = discount,
            buyQuantity = buyQuantity,
            getQuantity = getQuantity,
            minimumAmount = minimumAmount,
            availableTime = availableTimeDto?.toAvailableTime(),
            itemSelector = ItemSelector(),
        )
    }

    private fun AvailableTimeDto.toAvailableTime(): AvailableTime {
        return AvailableTime(
            end = end,
            start = start,
        )
    }

    private fun CouponDto.formatExpirationDate(): String {
        val couponExpirationDateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
        val date = LocalDate.parse(this.expirationDate)
        return date.format(couponExpirationDateFormatter)
    }
}
