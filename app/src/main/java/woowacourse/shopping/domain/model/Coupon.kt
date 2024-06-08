package woowacourse.shopping.domain.model

import java.time.LocalDate

sealed interface Coupon {
    val id: Int
    val code: String
    val description: String
    val expirationDate: LocalDate

    fun isExpired(currentDate: LocalDate = LocalDate.now()): Boolean {
        if (currentDate.isEqual(expirationDate)) return true
        return currentDate.isAfter(expirationDate)
    }

    fun totalOrderPrice(cartItems: List<CartItem>): Int {
        return cartItems.sumOf { it.totalPrice() }
    }

    fun available(cartItems: List<CartItem>): Boolean

    fun discountPrice(cartItems: List<CartItem>): Int

    companion object {
        const val INVALID_DISCOUNT_MESSAGE = "적용할 수 없는 쿠폰입니다."
        const val DELIVERY_FEE = 3000
    }
}
