package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import woowacourse.shopping.domain.model.coupon.Coupon
import java.time.LocalDate

@Serializable
@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("discountType")
sealed interface CouponResponse {
    val id: Long
    val code: String
    val description: String
    val expirationDate: LocalDate
}

fun CouponResponse.toDomain(): Coupon =
    when (this) {
        is BuyXGetYCoupon -> this.toDomain()
        is FixedDiscountCoupon -> this.toDomain()
        is PercentageDiscountCoupon -> this.toDomain()
        is FreeShippingCoupon -> this.toDomain()
    }
