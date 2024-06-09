package woowacourse.shopping.data.payment.remote.dto

import woowacourse.shopping.data.payment.remote.dto.AvailableTimeDto.Companion.toDomain
import woowacourse.shopping.domain.model.coupon.BuyXGetYCoupon
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.FixedDiscountCoupon
import woowacourse.shopping.domain.model.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.model.coupon.PercentageDiscountCoupon

data class CouponDto(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discount: Int?,
    val minimumAmount: Int?,
    val buyQuantity: Int?,
    val getQuantity: Int?,
    val availableTimeDto: AvailableTimeDto?,
    val discountType: String,
) {
    companion object {
        fun CouponDto.toBuyXGetYCoupon(): Coupon =
            BuyXGetYCoupon(
                id = id,
                code = code,
                description = description,
                expirationDate = expirationDate,
                discountType = discountType,
                buyQuantity = buyQuantity ?: throw IllegalStateException(),
                getQuantity = getQuantity ?: throw IllegalStateException(),
            )

        fun CouponDto.toFixedDiscountCoupon(): Coupon =
            FixedDiscountCoupon(
                id = id,
                code = code,
                description = description,
                expirationDate = expirationDate,
                discountType = discountType,
                discount = discount ?: throw IllegalStateException(),
                minimumAmount = minimumAmount ?: throw IllegalStateException(),
            )

        fun CouponDto.toFreeShippingCoupon(): Coupon =
            FreeShippingCoupon(
                id = id,
                code = code,
                description = description,
                expirationDate = expirationDate,
                discountType = discountType,
                minimumAmount = minimumAmount ?: throw IllegalStateException(),
            )

        fun CouponDto.toPercentageDiscountCoupon(): Coupon =
            PercentageDiscountCoupon(
                id = id,
                code = code,
                description = description,
                expirationDate = expirationDate,
                discountType = discountType,
                discount = discount ?: throw IllegalStateException(),
                availableTime = availableTimeDto?.toDomain() ?: throw IllegalStateException(),
            )
    }
}
