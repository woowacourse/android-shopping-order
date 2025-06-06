package woowacourse.shopping.domain.coupon

import androidx.annotation.CallSuper
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import java.time.LocalDate
import java.time.LocalTime

sealed class Coupon {
    abstract val id: Long
    abstract val description: String
    abstract val code: String
    abstract val explanationDate: LocalDate
    abstract val discountType: DiscountType
    open val minimumAmount: Int? = null
    open val availableStartTime: LocalTime? = null
    open val availableEndTime: LocalTime? = null

    @CallSuper
    open fun isAvailable(
        shoppingCartProductToOrder: List<ShoppingCartProduct>,
        today: LocalDate = LocalDate.now(),
        now: LocalTime = LocalTime.now(),
    ): Boolean {
        val totalOrderAmount = shoppingCartProductToOrder.sumOf { it.price }

        if (availableEndTime != null && availableStartTime != null) {
            if (now.isBefore(availableStartTime) || now.isAfter(availableEndTime)) {
                return false
            }
        }

        if (minimumAmount != null && totalOrderAmount < minimumAmount!!) {
            return false
        }

        if (explanationDate.isBefore(today)) {
            return false
        }
        return true
    }

    abstract fun disCountAmount(shoppingCartProductToOrder: List<ShoppingCartProduct>): Int

    companion object {
        const val DEFAULT_SHIPPING_FEE = 3000
    }
}
