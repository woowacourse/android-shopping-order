package woowacourse.shopping.domain.model

import java.time.LocalDate

sealed interface Coupon {
    val id: Int
    val code: String
    val description: String
    val expirationDate: LocalDate

    fun available(cartItems: List<CartItem>): Boolean

    fun discountPrice(cartItems: List<CartItem>): Int

    companion object {
        const val DELIVERY_FEE = 3000
    }
}
