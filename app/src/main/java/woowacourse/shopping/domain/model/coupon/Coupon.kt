package woowacourse.shopping.domain.model.coupon

sealed class Coupon : BaseCoupon {
    var isSelected: Boolean = false
    val hasMinimumAmount: Boolean
        get() = minimumAmount.amount > 0

    data class Fixed(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationPeriod: ExpirationPeriod,
        override val minimumAmount: MinimumAmount,
        val discountedAmount: DiscountedAmount,
    ) : Coupon()

    data class BonusGoods(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationPeriod: ExpirationPeriod,
        override val minimumAmount: MinimumAmount,
        val calculateBonusGoods: CalculateBonusGoods,
    ) : Coupon()

    data class FreeShipping(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationPeriod: ExpirationPeriod,
        override val minimumAmount: MinimumAmount,
    ) : Coupon()

    data class Percentage(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationPeriod: ExpirationPeriod,
        override val minimumAmount: MinimumAmount,
        val discountedAmount: DiscountedAmount,
    ) : Coupon()
}
