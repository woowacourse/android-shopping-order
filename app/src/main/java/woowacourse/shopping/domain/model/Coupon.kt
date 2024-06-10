package woowacourse.shopping.domain.model

import woowacourse.shopping.ui.model.CouponUi
import java.time.LocalDate

sealed class Coupon {
    abstract val id: Long
    abstract val description: String
    abstract val expirationDate: LocalDate

    fun isExpired(orders: Orders): Boolean = orders.orderDateTime.toLocalDate() > expirationDate

    abstract fun isSatisfiedPolicy(orders: Orders): Boolean

    abstract fun discountAmount(orders: Orders): Int
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