package woowacourse.shopping.order

import woowacourse.shopping.product.catalog.ProductUiModel

data class Coupon(
    val availableTime: AvailableTime?,
    val buyQuantity: Int,
    val code: String,
    val description: String,
    val discount: Int,
    val discountType: String,
    val expirationDate: String,
    val getQuantity: Int,
    val id: Long,
    val minimumAmount: Int,
) {
    val couponType
        get() =
            when (discountType) {
                "fixed" -> CouponType.Fixed
                "buyXgetY" -> CouponType.BuyAndGet
                "freeShipping" -> CouponType.FreeShipping
                "percentage" -> CouponType.Percentage
                else -> CouponType.Fixed
            }

    fun isConditionMet(products: List<ProductUiModel>) = couponType.isConditionMet(this, products)
}
