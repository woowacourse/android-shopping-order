
package woowacourse.shopping.domain.model

import java.time.LocalDate

data class FreeShippingCoupon(
    override val id: Long,
    override val description: String,
    override val expirationDate: LocalDate,
    val minimumAmount: Int,
    ) : Coupon() {
        override fun isSatisfiedPolicy(orders: Orders): Boolean = overThanMinimumOrderPrice(orders)

        private fun overThanMinimumOrderPrice(orders: Orders) = orders.totalPrice >= minimumAmount

        override fun discountAmount(orders: Orders): Int = orders.deliveryFee
    }
