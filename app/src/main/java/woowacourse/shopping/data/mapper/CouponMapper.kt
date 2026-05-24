package woowacourse.shopping.data.mapper

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import woowacourse.shopping.data.remote.dto.CouponResponseDto
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.CouponType
import woowacourse.shopping.domain.model.coupon.CouponTypes

object CouponMapper {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun toDomain(dto: CouponResponseDto): Coupon {
        val rate = when {
            dto.discountType == "PERCENT" && dto.discount != null -> dto.discount / 100.0
            else -> null
        }

        val expireAt = dto.expirationDate.let {
            runCatching { LocalDate.parse(it, dateFormatter) }.getOrNull()
        }

        return Coupon(
            code = dto.code,
            type = CouponTypes.fromCode(dto.code),
            amount = if (dto.discountType == "FIXED") dto.discount else null,
            rate = rate,
            minOrderAmount = dto.minimumAmount,
            expireAt = expireAt,
        )
    }
}


