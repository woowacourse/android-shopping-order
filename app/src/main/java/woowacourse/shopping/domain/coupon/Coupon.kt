package woowacourse.shopping.domain.coupon

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.domain.CartProduct
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Parcelize
data class Coupon(
    val id: Int,
    val code: String,
    val expirationDate: String,
    val discountType: String,
    val description: String,
    val discount: Int,
    val minimumAmount: Int,
    val buyQuantity: Int,
    val getQuantity: Int,
    val availableTime: AvailableTime?,
    var isSelected: Boolean = false,
) : Parcelable {

    fun discountPrice(totalPrice: Int, orderItems: List<CartProduct>): Int {
        if (!isAvailable()) return NO_DISCOUNT
        return when (code) {
            CouponType.FIXED5000.name -> applyFixed5000(totalPrice)
            CouponType.BOGO.name -> applyBogo(orderItems)
            CouponType.FREESHIPPING.name -> applyFreeShipping(totalPrice)
            CouponType.MIRACLESALE.name -> applyMiracleSale(totalPrice)
            else -> NO_DISCOUNT
        }
    }

    private fun isAvailable(): Boolean {
        val formatter = DateTimeFormatter.ofPattern(EXPIRATION_DATE_PATTERN)
        val expirationLocalDate = LocalDate.parse(expirationDate, formatter)
        return expirationLocalDate.isAfter(LocalDate.now())
    }

    private fun applyFixed5000(totalPrice: Int): Int {
        return if (totalPrice >= minimumAmount) discount else NO_DISCOUNT
    }

    private fun applyBogo(orderItems: List<CartProduct>): Int {
        val eligibleItems = orderItems.filter { it.quantity >= BOGO_CONDITION }
        return eligibleItems.maxByOrNull { it.price }?.price?.toInt() ?: NO_DISCOUNT
    }

    private fun applyFreeShipping(totalPrice: Int): Int {
        return if (totalPrice >= minimumAmount) DELIVERY_FEE else NO_DISCOUNT
    }

    private fun applyMiracleSale(totalPrice: Int): Int {
        val orderTime = LocalTime.now()
        availableTime?.let {
            val startTime = LocalTime.parse(it.start)
            val endTime = LocalTime.parse(it.end)
            return if (orderTime.isAfter(startTime) && orderTime.isBefore(endTime)) {
                (totalPrice * MIRACLE_SALE_DISCOUNT_RATE).toInt()
            } else NO_DISCOUNT
        }
        return NO_DISCOUNT
    }

    companion object {
        const val NO_DISCOUNT = 0
        const val DELIVERY_FEE = 3000
        const val BOGO_CONDITION = 3
        const val MIRACLE_SALE_DISCOUNT_RATE = 0.3
        const val EXPIRATION_DATE_PATTERN = "yyyy-MM-dd"
    }
}
