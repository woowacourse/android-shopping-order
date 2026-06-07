package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.remote.dto.CouponResponseDto
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.CouponType
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object CouponMapper {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun toDomain(dto: CouponResponseDto): Coupon {
        val couponType = CouponType.fromApiCode(dto.discountType)

        val rate =
            when {
                couponType == CouponType.MIRACLESALE && dto.discount != null -> dto.discount / 100.0
                else -> null
            }

        val amount = if (couponType == CouponType.FIXED5000) dto.discount else null

        val expireAt =
            dto.expirationDate.let {
                runCatching { LocalDate.parse(it, dateFormatter) }.getOrNull()
            }

        val availableStartTime = dto.availableTime?.start?.let { runCatching { LocalTime.parse(it) }.getOrNull() }
        val availableEndTime = dto.availableTime?.end?.let { runCatching { LocalTime.parse(it) }.getOrNull() }

        return Coupon(
            code = dto.code,
            description = dto.description,
            type = couponType,
            amount = amount,
            rate = rate,
            minOrderAmount = dto.minimumAmount,
            expireAt = expireAt,
            buyQuantity = dto.buyQuantity,
            getQuantity = dto.getQuantity,
            availableStartTime = availableStartTime,
            availableEndTime = availableEndTime,
        )
    }
}
