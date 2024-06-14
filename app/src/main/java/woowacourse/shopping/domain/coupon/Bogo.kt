package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.CartProduct
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Bogo(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val expirationDate: String,
    override val buyQuantity: Int,
    override val getQuantity: Int,
    override val discountType: String,
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

        return currentDate <= expirationDate && cartProducts.any { it.quantity >= buyQuantity }
    }

    override fun getPriceDiscount(cartProducts: List<CartProduct>): Int {
        return cartProducts.filter { it.quantity >= buyQuantity }.maxOf { it.price }.toInt() * getQuantity
    }

    override fun getDeliveryDiscount(cartProducts: List<CartProduct>): Int {
        return 0
    }
}
