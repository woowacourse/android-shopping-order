package woowacourse.shopping.data.dto.response.coupon

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import woowacourse.shopping.domain.model.coupon.Coupon

@Serializable
@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("discountType")
sealed interface CouponResponseDto {
    val id: Int
    val code: String
    val description: String
    val expirationDate: String
}

fun CouponResponseDto.toDomain(): Coupon =
    when (this) {
        is FixedDiscountCouponDto -> this.toDomain()
        is PercentageDiscountCouponDto -> this.toDomain()
        is BuyXGetYCouponDto -> this.toDomain()
        is FreeShippingCouponDto -> this.toDomain()
    }
