package woowacourse.shopping.presentation.model

import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.DiscountType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class CouponUiModel(
    val code: String,
    val description: String,
    val expirationDate: String,
    val discount: Int?,
    val buyQuantity: Int?,
    val getQuantity: Int?,
    val discountType: String,
    val minimumAmount: Int?,
    val availableTime: AvailableTimeUiModel?,
    val isSelected: Boolean = false,
)

fun CouponUiModel.toDomain(): Coupon =
    Coupon(
        code = code,
        description = description,
        expirationDate =
            LocalDate.parse(
                expirationDate,
                DateTimeFormatter.ofPattern("yyyy년 M월 d일"),
            ),
        discount = discount,
        discountType =
            discountType.toDiscountType(
                discount,
                buyQuantity = buyQuantity,
                getQuantity = getQuantity,
            ),
        minimumAmount = minimumAmount,
        availableTime = availableTime?.toDomain(),
        buyQuantity = buyQuantity,
        getQuantity = getQuantity,
    )

fun Coupon.toPresentation(): CouponUiModel =
    CouponUiModel(
        code = code,
        description = description,
        expirationDate = expirationDate.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일")),
        discount = discount,
        buyQuantity = buyQuantity,
        getQuantity = getQuantity,
        discountType = discountType.toPresentation(),
        minimumAmount = minimumAmount,
        availableTime = availableTime?.toPresentation(),
        isSelected = false,
    )

fun DiscountType.toPresentation(): String =
    when (this) {
        is DiscountType.FixedAmount -> "fixed"
        is DiscountType.Percentage -> "percentage"
        is DiscountType.FreeShipping -> "free_shipping"
        is DiscountType.BuyXGetY -> "buyXgetY"
    }

fun String.toDiscountType(
    discount: Int?,
    buyQuantity: Int?,
    getQuantity: Int?,
): DiscountType =
    when (this) {
        "fixed" -> DiscountType.FixedAmount(discount ?: 0)
        "percentage" -> DiscountType.Percentage(discount ?: 0)
        "free_shipping" -> DiscountType.FreeShipping
        "buyXgetY" ->
            DiscountType.BuyXGetY(
                buyQuantity = buyQuantity ?: 0,
                getQuantity = getQuantity ?: 0,
            )

        else -> throw IllegalArgumentException("알 수 없는 discountType: $this")
    }
