package woowacourse.shopping.data.dto.response.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.coupon.PercentageCoupon

@Serializable
@SerialName("percentage")
data class PercentageDiscountCouponDto(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
    val discount: Int,
    val availableTime: AvailableTimeDto,
) : CouponResponseDto

fun PercentageDiscountCouponDto.toDomain(): PercentageCoupon =
    PercentageCoupon(
        id,
        code,
        description,
        expirationDate,
        discount,
        availableTime.toDomain(),
    )
