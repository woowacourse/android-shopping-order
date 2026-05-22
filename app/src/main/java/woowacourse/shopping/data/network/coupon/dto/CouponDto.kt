package woowacourse.shopping.data.network.coupon.dto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import woowacourse.shopping.domain.coupon.BuyXGetYCoupon
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.FixedDiscountCoupon
import woowacourse.shopping.domain.coupon.PercentageDiscountCoupon
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("discountType")
sealed interface CouponDto {
    val id: Long?
    val code: String
    val description: String
    val expirationDate: String
    val discountType: String?
}

fun CouponDto.toDomain(): Coupon {
    return when (this) {
        is FixedDiscountCouponDto -> FixedDiscountCoupon(
            code = code,
            description = description,
            expirationDate = LocalDate.parse(expirationDate),
            discount = discount ?: 0,
            minimumAmount = minimumAmount ?: 0
        )

        is BuyXGetYCouponDto -> BuyXGetYCoupon(
            code = code,
            description = description,
            expirationDate = LocalDate.parse(expirationDate),
            buyQuantity = buyQuantity ?: 0,
            getQuantity = getQuantity ?: 0
        )

        is PercentageDiscountCouponDto -> PercentageDiscountCoupon(
            code = code,
            description = description,
            expirationDate = LocalDate.parse(expirationDate),
            discount = discount ?: 0,
            availableTime = availableTime ?: AvailableTime(
                start = LocalTime.MIN.toString(),
                end = LocalTime.MAX.toString()
            )
        )

        is FreeShippingCouponDto -> FixedDiscountCoupon(
            code = code,
            description = description,
            expirationDate = LocalDate.parse(expirationDate),
            minimumAmount = minimumAmount ?: 0,
            discount = 0
        )
    }
}
