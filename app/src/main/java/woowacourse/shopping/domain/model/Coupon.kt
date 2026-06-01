package woowacourse.shopping.domain.model

data class Coupon(
    val id: Long,
    val code: String,
    val description: String,
    val expirationDate: String,
    val benefit: CouponBenefit,
)

sealed interface CouponBenefit {
    data class AmountDiscount(
        val discountAmount: Int,
        val minimumOrderAmount: Int,
    ) : CouponBenefit

    data class BuyTwoGetOne(
        val requiredQuantity: Int,
        val freeQuantity: Int,
    ) : CouponBenefit

    data class FreeShipping(
        val minimumOrderAmount: Int,
    ) : CouponBenefit

    data class MorningDiscount(
        val startTime: String,
        val endTime: String,
        val discountRate: Int,
    ) : CouponBenefit

    data class Unknown(
        val discountType: String,
        val discount: Int,
        val minimumOrderAmount: Int,
    ) : CouponBenefit
}
