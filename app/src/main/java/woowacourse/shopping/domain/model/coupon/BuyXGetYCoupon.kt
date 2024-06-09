package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.ui.model.CouponUiModel

data class BuyXGetYCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
    override val discountType: String,
    val buyQuantity: Int,
    val getQuantity: Int,
): Coupon {
    companion object {
        fun BuyXGetYCoupon.toUiModel() =
            CouponUiModel(
                id = id,
                description = description,
                expirationDate = expirationDate,
                minimumAmount = null,
                discountType = discountType,
            )
    }
}
