package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.CartProduct
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Fixed(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val discountType: String,
    override val expirationDate: String,
    override val discount: Int,
    override val minimumAmount: Int,
) : Coupon(
    id,
    code,
    description,
    discountType,
    expirationDate,
) {
    override fun isValid(cartProducts: List<CartProduct>): Boolean {
        val currentDate = LocalDate.now()
        val expirationDate = LocalDate.parse(expirationDate, DateTimeFormatter.ISO_DATE)

        return currentDate <= expirationDate && cartProducts.sumOf { it.quantity * it.price } >= minimumAmount
    }

    override fun getPriceDiscount(cartProducts: List<CartProduct>): Int {
        return DISCOUNT
    }

    override fun getDeliveryDiscount(cartProducts: List<CartProduct>): Int {
        return 0
    }

    companion object {
        const val DISCOUNT = 5_000
    }
}