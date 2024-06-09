package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.ui.model.CouponUiModel

data class PercentageDiscountCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
    override val discountType: String,
    val discount: Int,
    val availableTime: AvailableTime,
): Coupon {
    companion object {
        fun PercentageDiscountCoupon.toUiModel() =
            CouponUiModel(
                id = id,
                description = description,
                expirationDate = expirationDate,
                minimumAmount = null,
                discountType = discountType,
            )
    }
}
