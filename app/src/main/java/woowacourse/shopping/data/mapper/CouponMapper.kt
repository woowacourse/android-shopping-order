package woowacourse.shopping.data.mapper

import android.util.Log
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import woowacourse.shopping.data.remote.dto.CouponResponseDto
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.CouponType
import woowacourse.shopping.domain.model.coupon.CouponTypes

object CouponMapper {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private const val TAG = "CouponMapper"

    fun toDomain(dto: CouponResponseDto): Coupon {

        val normalizedDiscountType = dto.discountType.trim().lowercase()
        val couponType = determineCouponType(normalizedDiscountType)

        val rate = when {
            normalizedDiscountType == "percentage" && dto.discount != null -> dto.discount / 100.0
            else -> null
        }

        val expireAt = dto.expirationDate.let {
            runCatching { LocalDate.parse(it, dateFormatter) }.getOrNull()
        }

        val availableStartTime = dto.availableTime?.start?.let { runCatching { LocalTime.parse(it) }.getOrNull() }
        val availableEndTime = dto.availableTime?.end?.let { runCatching { LocalTime.parse(it) }.getOrNull() }

        return Coupon(
            code = dto.code,
            description = dto.description,
            type = couponType,
            amount = if (normalizedDiscountType == "fixed") dto.discount else null,
            rate = rate,
            minOrderAmount = dto.minimumAmount,
            expireAt = expireAt,
            buyQuantity = dto.buyQuantity,
            getQuantity = dto.getQuantity,
            availableStartTime = availableStartTime,
            availableEndTime = availableEndTime,
        )
    }

    private fun determineCouponType(normalizedDiscountType: String): CouponType {
        return when (normalizedDiscountType) {
            "fixed" -> CouponTypes.FIXED5000
            "percentage" -> CouponTypes.MIRACLESALE
            "buyxgety" -> CouponTypes.BOGO
            "freeshipping" -> CouponTypes.FREESHIPPING
            else -> CouponTypes.UNKNOWN
        }
    }
}
