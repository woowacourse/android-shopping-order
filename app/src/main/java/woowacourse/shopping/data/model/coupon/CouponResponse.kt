package woowacourse.shopping.data.model.coupon

import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.coupon.Coupon
import java.time.LocalDate

@Serializable
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
