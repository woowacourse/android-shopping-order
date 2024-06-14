package woowacourse.shopping.data.coupon.remote.dto

import woowacourse.shopping.data.coupon.remote.dto.AvailableTimeDto.Companion.toDomain
import woowacourse.shopping.domain.model.coupon.BuyXGetYCoupon
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.coupon.FixedDiscountCoupon
import woowacourse.shopping.domain.model.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.model.coupon.PercentageDiscountCoupon
import java.time.LocalDate

data class CouponDto(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: String,
    val discount: Int?,
    val minimumAmount: Int?,
    val buyQuantity: Int?,
    val getQuantity: Int?,
    val availableTime: AvailableTimeDto?,
    val discountType: String,
) {
    companion object {
        private const val FAILED_TO_GET_VALUE_FROM_SERVER = "서버에서 %s 값을 받지 못했습니다."
        private const val UNKNOWN_DISCOUNT_TYPE = "정의되지 않은 할인 유형 입니다."

        fun CouponDto.toDomain(): Coupon =
            when (discountType) {
                BuyXGetYCoupon.TYPE -> toBuyXGetYCoupon()
                FixedDiscountCoupon.TYPE -> toFixedDiscountCoupon()
                FreeShippingCoupon.TYPE -> toFreeShippingCoupon()
                PercentageDiscountCoupon.TYPE -> toPercentageDiscountCoupon()
                else -> throw IllegalArgumentException(UNKNOWN_DISCOUNT_TYPE)
            }

        private fun CouponDto.toBuyXGetYCoupon(): Coupon =
            BuyXGetYCoupon(
                id = id,
                code = code,
                description = description,
                expirationDate = LocalDate.parse(expirationDate),
                discountType = discountType,
                buyQuantity = buyQuantity ?: throw IllegalStateException(FAILED_TO_GET_VALUE_FROM_SERVER.format("buyQuantity")),
                getQuantity = getQuantity ?: throw IllegalStateException(FAILED_TO_GET_VALUE_FROM_SERVER.format("getQuantity")),
            )

        private fun CouponDto.toFixedDiscountCoupon(): Coupon =
            FixedDiscountCoupon(
                id = id,
                code = code,
                description = description,
                expirationDate = LocalDate.parse(expirationDate),
                discountType = discountType,
                discount = discount ?: throw IllegalStateException(FAILED_TO_GET_VALUE_FROM_SERVER.format("discount")),
                minimumAmount = minimumAmount ?: throw IllegalStateException(FAILED_TO_GET_VALUE_FROM_SERVER.format("minimumAmount")),
            )

        private fun CouponDto.toFreeShippingCoupon(): Coupon =
            FreeShippingCoupon(
                id = id,
                code = code,
                description = description,
                expirationDate = LocalDate.parse(expirationDate),
                discountType = discountType,
                minimumAmount = minimumAmount ?: throw IllegalStateException(FAILED_TO_GET_VALUE_FROM_SERVER.format("minimumAmount")),
            )

        private fun CouponDto.toPercentageDiscountCoupon(): Coupon =
            PercentageDiscountCoupon(
                id = id,
                code = code,
                description = description,
                expirationDate = LocalDate.parse(expirationDate),
                discountType = discountType,
                discount = discount ?: throw IllegalStateException(FAILED_TO_GET_VALUE_FROM_SERVER.format("discount")),
                availableTime = availableTime?.toDomain() ?: throw IllegalStateException(FAILED_TO_GET_VALUE_FROM_SERVER.format("availableTime")),
            )
    }
}
