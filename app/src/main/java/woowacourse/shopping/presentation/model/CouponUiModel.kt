package woowacourse.shopping.presentation.model

import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.DiscountType
import java.time.LocalDate

data class CouponUiModel(
    val code: String,
    val description: String,
    val expirationDate: LocalDate,
    val discount: Int?,
    val buyQuantity: Int?,
    val getQuantity: Int?,
    val discountType: DiscountTypeUi,
    val minimumAmount: Int?,
    val availableTime: AvailableTimeUiModel?,
    val isSelected: Boolean = false,
)

fun CouponUiModel.toDomain(): Coupon =
    Coupon(
        code = code,
        description = description,
        expirationDate = expirationDate,
        discount = discount,
        discountType = discountType.toDomain(discount, buyQuantity, getQuantity),
        minimumAmount = minimumAmount,
        availableTime = availableTime?.toDomain(),
        buyQuantity = buyQuantity,
        getQuantity = getQuantity,
    )

fun Coupon.toPresentation(): CouponUiModel =
    CouponUiModel(
        code = code,
        description = description,
        expirationDate = expirationDate,
        discount = discount,
        buyQuantity = buyQuantity,
        getQuantity = getQuantity,
        discountType = discountType.toPresentation(),
        minimumAmount = minimumAmount,
        availableTime = availableTime?.toPresentation(),
        isSelected = false,
    )

fun DiscountType.toPresentation(): DiscountTypeUi =
    when (this) {
        is DiscountType.FixedAmount -> DiscountTypeUi.FIXED
        is DiscountType.Percentage -> DiscountTypeUi.PERCENTAGE
        is DiscountType.FreeShipping -> DiscountTypeUi.FREE_SHIPPING
        is DiscountType.BuyXGetY -> DiscountTypeUi.BUY_X_GET_Y
    }

fun DiscountTypeUi.toDomain(
    discount: Int?,
    buyQuantity: Int?,
    getQuantity: Int?,
): DiscountType =
    when (this) {
        DiscountTypeUi.FIXED -> DiscountType.FixedAmount(discount ?: 0)
        DiscountTypeUi.PERCENTAGE -> DiscountType.Percentage(discount ?: 0)
        DiscountTypeUi.FREE_SHIPPING -> DiscountType.FreeShipping
        DiscountTypeUi.BUY_X_GET_Y -> DiscountType.BuyXGetY(buyQuantity ?: 0, getQuantity ?: 0)
    }
