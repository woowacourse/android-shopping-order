package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.ui.model.CouponUiModel

data class FixedDiscountCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
    override val discountType: String,
    val discount: Int,
    val minimumAmount: Int,
): Coupon {
    companion object {
        fun FixedDiscountCoupon.toUiModel() =
            CouponUiModel(
                id = id,
                description = description,
                expirationDate = expirationDate,
                minimumAmount = minimumAmount,
                discountType = discountType,
            )
    }
}
