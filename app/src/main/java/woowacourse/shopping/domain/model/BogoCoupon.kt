package woowacourse.shopping.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class BogoCoupon(
    override val id: Int,
    override val code: String,
    override val description: String,
    override val discountType: String,
    override val expirationDate: LocalDate,
    val buyQuantity: Int,
    val getQuantity: Int,
) : Coupon {
    override fun isUsable(
        today: LocalDateTime,
        order: List<Cart>,
        payment: Int,
    ): Boolean {
        if (isExpired(today.toLocalDate())) return false
        if (isEnoughQuantity(order).not()) return false
        return true
    }

    override fun applyToPayment(
        origin: Payment,
        order: List<Cart>,
    ): Payment {
        val mostExpensiveProductPrice = findTargetProductForBogo(buyQuantity, order)

        val totalPayment = origin.totalPayment - mostExpensiveProductPrice
        return origin.copy(
            couponDiscount = -mostExpensiveProductPrice,
            totalPayment = totalPayment,
        )
    }

    fun findTargetProductForBogo(
        standardQuantity: Int,
        buyCarts: List<Cart>,
    ): Int =
        buyCarts
            .filter { it.quantity >= standardQuantity }
            .maxByOrNull { it.product.price }
            ?.product
            ?.price
            ?: throw IllegalArgumentException("해당 상품을 찾을 수 없습니다")

    private fun isEnoughQuantity(carts: List<Cart>): Boolean {
        val quantities = carts.map { it.quantity }
        return quantities.any { it >= buyQuantity + getQuantity }
    }
}
