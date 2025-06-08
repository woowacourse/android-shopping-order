package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.CouponPolicyContext
import woowacourse.shopping.data.model.CouponResponse
import java.time.LocalDate
import java.time.LocalTime

sealed class CouponPolicy {
    abstract fun isApplicable(
        coupon: CouponResponse,
        context: CouponPolicyContext,
    ): Boolean

    object Fixed5000 : CouponPolicy() {
        override fun isApplicable(
            coupon: CouponResponse,
            context: CouponPolicyContext,
        ): Boolean {
            val date = LocalDate.parse(coupon.expirationDate)
            val isValid = context.currentDateTime.toLocalDate().isBefore(date)

            return coupon.minimumAmount != null && context.totalAmount >= coupon.minimumAmount && isValid
        }
    }

    object FreeShipping : CouponPolicy() {
        override fun isApplicable(
            coupon: CouponResponse,
            context: CouponPolicyContext,
        ): Boolean {
            val date = LocalDate.parse(coupon.expirationDate)
            val isValid = context.currentDateTime.toLocalDate().isBefore(date)

            return coupon.minimumAmount != null && context.totalAmount >= coupon.minimumAmount && isValid
        }
    }

    object MiracleSale : CouponPolicy() {
        override fun isApplicable(
            coupon: CouponResponse,
            context: CouponPolicyContext,
        ): Boolean {
            val date = LocalDate.parse(coupon.expirationDate)
            val time = context.currentDateTime.toLocalTime()
            val isValid = context.currentDateTime.toLocalDate().isBefore(date)

            val availableTime = coupon.availableTime
            return if (availableTime != null && isValid) {
                val startTime = LocalTime.parse(availableTime.start)
                val endTime = LocalTime.parse(availableTime.end)
                time in startTime..endTime
            } else {
                false
            }
        }
    }

    object Bogo : CouponPolicy() {
        override fun isApplicable(
            coupon: CouponResponse,
            context: CouponPolicyContext,
        ): Boolean {
            val date = LocalDate.parse(coupon.expirationDate)
            val isValid = context.currentDateTime.toLocalDate().isBefore(date)

            val buyQuantity = coupon.buyQuantity ?: 0
            val getQuantity = coupon.getQuantity ?: 0
            val requiredQuantity = buyQuantity + getQuantity

            val applicableProducts = context.orderProducts.filter { it.quantity >= requiredQuantity }
            return applicableProducts.isNotEmpty() && isValid
        }
    }

    object Default : CouponPolicy() {
        override fun isApplicable(
            coupon: CouponResponse,
            context: CouponPolicyContext,
        ): Boolean {
            return true
        }
    }
}
