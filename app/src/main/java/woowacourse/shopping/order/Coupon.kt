package woowacourse.shopping.order

import woowacourse.shopping.product.catalog.ProductUiModel
import java.time.LocalDate
import java.time.LocalTime

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

    fun isAvailableTime(currentTime: LocalTime): Boolean {
        if (availableTime == null) return true

        val startTime = LocalTime.parse(availableTime.start)
        val endTime = LocalTime.parse(availableTime.end)

        return !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime)
    }

    fun isNotExpired(currentDate: LocalDate): Boolean {
        val expiredDate = LocalDate.parse(expirationDate)
        return !currentDate.isAfter(expiredDate)
    }
}
