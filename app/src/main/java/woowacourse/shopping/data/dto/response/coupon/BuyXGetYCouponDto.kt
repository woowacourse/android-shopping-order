package woowacourse.shopping.data.dto.response.coupon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.coupon.BuyXGetYCoupon

@Serializable
@SerialName("buyXgetY")
data class BuyXGetYCouponDto(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
    val buyQuantity: Int,
    val getQuantity: Int,
) : CouponResponseDto

fun BuyXGetYCouponDto.toDomain(): BuyXGetYCoupon =
    BuyXGetYCoupon(
        id,
        code,
        description,
        expirationDate,
        buyQuantity,
        getQuantity,
    )
