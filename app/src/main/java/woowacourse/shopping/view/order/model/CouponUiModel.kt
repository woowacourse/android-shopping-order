package woowacourse.shopping.view.order.model

import java.time.LocalDate
import java.time.LocalTime

sealed class CouponUiModel(
    open val id: Long,
    open val code: String,
    open val description: String,
    open val expirationDate: LocalDate,
    open val discountType: String,
    open val minimumAmount: Int = 0,
    open var isSelected: Boolean = false,
) {
    data class FixedDiscountCouponUiModel(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        override val discountType: String,
        override val minimumAmount: Int,
        val discount: Int,
        override var isSelected: Boolean = false,
    ) : CouponUiModel(id, code, description, expirationDate, discountType, minimumAmount)

    data class BogoCouponUiModel(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        override val discountType: String,
        val buyQuantity: Int,
        val getQuantity: Int,
        override var isSelected: Boolean = false,
    ) : CouponUiModel(id, code, description, expirationDate, discountType)

    data class FreeShippingCouponUiModel(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        override val discountType: String,
        override val minimumAmount: Int,
        override var isSelected: Boolean = false,
    ) : CouponUiModel(id, code, description, expirationDate, discountType, minimumAmount)

    data class TimeBasedDiscountCouponUiModel(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        override val discountType: String,
        val discount: Int,
        val availableTimeStart: LocalTime,
        val availableTimeEnd: LocalTime,
        override var isSelected: Boolean = false,
    ) : CouponUiModel(id, code, description, expirationDate, discountType)
}
