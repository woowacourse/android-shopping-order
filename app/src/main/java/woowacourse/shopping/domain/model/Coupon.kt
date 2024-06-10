package woowacourse.shopping.domain.model

import woowacourse.shopping.ui.model.CouponUi
import java.time.LocalDate

sealed class Coupon {
    abstract val id: Long
    abstract val description: String
    abstract val expirationDate: LocalDate

    fun discount(orders: Orders): Int = when {
        isExpired(orders) -> NOT_DISCOUNTED
        isSatisfiedPolicy(orders) -> discountAmount(orders)
        else -> NOT_DISCOUNTED

    }

    fun isExpired(orders: Orders): Boolean = orders.orderDateTime.toLocalDate() > expirationDate

    abstract fun isSatisfiedPolicy(orders: Orders): Boolean

    abstract fun discountAmount(orders: Orders): Int


    companion object {
        const val NOT_DISCOUNTED = 0
    }


}

fun Coupon.toUi(): CouponUi = when (this) {
    is FixedAmountCoupon -> CouponUi(
        id = id,
        description = description,
        expirationDate = expirationDate,
        minimumAmount = minimumAmount,
    )

    is FreeShippingCoupon -> CouponUi(
        id = id,
        description = description,
        expirationDate = expirationDate,
        minimumAmount = minimumAmount,
    )

    is BoGoCoupon -> CouponUi(
        id = id,
        description = description,
        expirationDate = expirationDate,
        minimumAmount = null
    )


    is PercentageCoupon -> CouponUi(
        id = id,
        description = description,
        expirationDate = expirationDate,
        minimumAmount = null
    )
}