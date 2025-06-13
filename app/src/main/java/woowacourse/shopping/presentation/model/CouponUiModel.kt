package woowacourse.shopping.presentation.model

import woowacourse.shopping.domain.model.Coupon
import java.time.LocalDate

data class CouponUiModel(
    val id: Long,
    val description: String,
    val expirationDate: LocalDate,
    val minimumOrderAmount: Int?,
    val isSelected: Boolean,
)

fun Coupon.toUiModel(): CouponUiModel =
    when (this) {
        is Coupon.FixedAmountCoupon ->
            CouponUiModel(
                id = id,
                description = description,
                expirationDate = expirationDate,
                minimumOrderAmount = minimumAmount,
                isSelected = false,
            )

        is Coupon.FreeShippingCoupon ->
            CouponUiModel(
                id = id,
                description = description,
                expirationDate = expirationDate,
                minimumOrderAmount = minimumAmount,
                isSelected = false,
            )

        is Coupon.BuyXGetYCoupon ->
            CouponUiModel(
                id = id,
                description = description,
                expirationDate = expirationDate,
                minimumOrderAmount = null,
                isSelected = false,
            )

        is Coupon.PercentageCoupon ->
            CouponUiModel(
                id = id,
                description = description,
                expirationDate = expirationDate,
                minimumOrderAmount = null,
                isSelected = false,
            )
    }
