package woowacourse.shopping.data.dto.response.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.coupon.FreeShippingCoupon

@Serializable
@SerialName("freeShipping")
data class FreeShippingCouponDto(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
    val minimumAmount: Int,
) : CouponResponseDto

fun FreeShippingCouponDto.toDomain(): FreeShippingCoupon =
    FreeShippingCoupon(
        id,
        code,
        description,
        expirationDate,
        minimumAmount,
    )
