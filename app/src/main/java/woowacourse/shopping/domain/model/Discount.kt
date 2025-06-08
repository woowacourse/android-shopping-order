package woowacourse.shopping.domain.model

import java.time.LocalTime

sealed class Discount {
    abstract fun calculateDiscount(
        products: CartProducts,
        shippingFee: Int,
        now: LocalTime = LocalTime.now(),
    ): Int

    data class FixedAmount(
        val discountAmount: Int,
        val minimumAmount: Int,
    ) : Discount() {
        override fun calculateDiscount(
            products: CartProducts,
            shippingFee: Int,
            now: LocalTime,
        ): Int =
            if (products.totalPrice >= minimumAmount) {
                discountAmount
            } else {
                MINIMUM_AMOUNT
            }
    }

    data class BuyXGetYFree(
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : Discount() {
        override fun calculateDiscount(
            products: CartProducts,
            shippingFee: Int,
            now: LocalTime,
        ): Int {
            val freeProduct =
                products.value
                    .filter { it.quantity >= buyQuantity + getQuantity }
                    .maxByOrNull { it.product.price }
            return if (freeProduct != null) {
                freeProduct.product.price * getQuantity
            } else {
                MINIMUM_AMOUNT
            }
        }
    }

    data class FreeShipping(
        val minimumAmount: Int,
    ) : Discount() {
        override fun calculateDiscount(
            products: CartProducts,
            shippingFee: Int,
            now: LocalTime,
        ): Int =
            if (products.totalPrice >= minimumAmount) {
                shippingFee
            } else {
                MINIMUM_AMOUNT
            }
    }

    data class Percentage(
        val discountPercentage: Int,
        val startTime: LocalTime,
        val endTime: LocalTime,
    ) : Discount() {
        override fun calculateDiscount(
            products: CartProducts,
            shippingFee: Int,
            now: LocalTime,
        ): Int =
            if (now in startTime..endTime) {
                (products.totalPrice * discountPercentage) / 100
            } else {
                MINIMUM_AMOUNT
            }
    }

    companion object {
        private const val MINIMUM_AMOUNT = 0
    }
}
