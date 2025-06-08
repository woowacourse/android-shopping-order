package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.cart.Cart
import java.time.LocalDate
import java.time.LocalDateTime

interface Coupon {
    val couponId: Long
    val expirationDate: LocalDate

    fun isAvailable(cart: Cart, current: LocalDateTime): Boolean
}