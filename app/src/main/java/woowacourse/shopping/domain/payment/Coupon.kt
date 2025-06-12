package woowacourse.shopping.domain.payment

import woowacourse.shopping.domain.cart.CartItem
import java.time.LocalDate
import java.time.LocalTime

sealed interface Coupon {
    val id: Long
    val code: String
    val description: String
    val expirationDate: LocalDate

    fun discountAmount(
        cartItems: List<CartItem>,
        fee: Int,
    ): Int

    data class FixedDiscountCoupon(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val discount: Int,
        val minimumAmount: Int,
    ) : Coupon {
        override fun discountAmount(
            cartItems: List<CartItem>,
            fee: Int,
        ): Int {
            val totalAmount = cartItems.sumOf { it.price }
            return if (totalAmount < minimumAmount) {
                0
            } else {
                discount
            }
        }
    }

    data class BuyNGetNCoupon(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : Coupon {
        override fun discountAmount(
            cartItems: List<CartItem>,
            fee: Int,
        ): Int =
            cartItems
                .filter { it.quantity >= 3 }
                .maxByOrNull { cartItem: CartItem ->
                    cartItem.productPrice
                }?.productPrice ?: 0
    }

    data class FreeShippingCoupon(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val minimumAmount: Int,
    ) : Coupon {
        override fun discountAmount(
            cartItems: List<CartItem>,
            fee: Int,
        ): Int {
            val totalAmount = cartItems.sumOf { it.price }
            return if (totalAmount < minimumAmount) {
                0
            } else {
                fee
            }
        }
    }

    data class PercentageCoupon(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val discountRate: Double,
        val availableTime: AvailableTime,
    ) : Coupon {
        override fun discountAmount(
            cartItems: List<CartItem>,
            fee: Int,
        ): Int {
            val totalAmount = cartItems.sumOf { it.price }
            return (totalAmount * discountRate).toInt()
        }

        data class AvailableTime(
            val start: LocalTime,
            val end: LocalTime,
        )
    }
}
