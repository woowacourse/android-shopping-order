package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.CartProduct
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MiracleSale(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
    override val discount: Int,
    override val availableTimeStart: String,
    override val availableTimeEnd: String,
    override val discountType: String,
) : Coupon(id, code, description, discountType, expirationDate) {
    override fun isValid(cartProducts: List<CartProduct>): Boolean {
        val currentTime = LocalTime.now()
        val startTime = LocalTime.parse(availableTimeStart, DateTimeFormatter.ISO_TIME)
        val endTime = LocalTime.parse(availableTimeEnd, DateTimeFormatter.ISO_TIME)
        return currentTime.isAfter(startTime) && currentTime.isBefore(endTime)
    }

    override fun getPriceDiscount(cartProducts: List<CartProduct>): Int {
        return (cartProducts.sumOf { it.price * it.quantity } * DISCOUNT_PERCENTAGE).toInt()
    }

    override fun getDeliveryDiscount(cartProducts: List<CartProduct>): Int {
        return 0
    }

    companion object {
        const val DISCOUNT_PERCENTAGE = 0.3
    }
}