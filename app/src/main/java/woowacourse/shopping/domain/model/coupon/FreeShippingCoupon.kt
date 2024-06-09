package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.ui.model.CouponUiModel

data class FreeShippingCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
    override val discountType: String,
    val minimumAmount: Int,
): Coupon {
    companion object {
        fun FreeShippingCoupon.toUiModel() =
            CouponUiModel(
                id = id,
                description = description,
                expirationDate = expirationDate,
                minimumAmount = minimumAmount,
                discountType = discountType,
            )
    }
}
