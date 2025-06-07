package woowacourse.shopping.data.dto.response.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.coupon.FixedCoupon

@Serializable
@SerialName("fixed")
data class FixedDiscountCouponDto(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
    val discount: Int,
    val minimumAmount: Int,
) : CouponResponseDto

fun FixedDiscountCouponDto.toDomain(): FixedCoupon =
    FixedCoupon(
        id,
        code,
        description,
        expirationDate,
        discount,
        minimumAmount,
    )
