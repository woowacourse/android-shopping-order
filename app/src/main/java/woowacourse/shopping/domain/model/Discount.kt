package woowacourse.shopping.domain.model

import java.time.LocalTime

sealed class Discount {
    abstract fun calculateDiscount(
        products: List<CartProduct>,
        shippingFee: Int,
        now: LocalTime = LocalTime.now(),
    ): Int

    data class FixedAmount(
        val discountAmount: Int,
        val minimumAmount: Int,
    ) : Discount() {
        override fun calculateDiscount(
            products: List<CartProduct>,
            shippingFee: Int,
            now: LocalTime,
        ): Int {
            val totalPrice = products.sumOf { it.totalPrice }
            return if (totalPrice >= minimumAmount) discountAmount else 0
        }
    }

    data class BuyXGetYFree(
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : Discount() {
        override fun calculateDiscount(
            products: List<CartProduct>,
            shippingFee: Int,
            now: LocalTime,
        ): Int {
            val freeProduct =
                products
                    .filter { it.quantity >= buyQuantity + getQuantity }
                    .maxByOrNull { it.product.price }
            if (freeProduct == null) return 0
            return freeProduct.product.price * getQuantity
        }
    }

    data class FreeShipping(
        val minimumAmount: Int,
    ) : Discount() {
        override fun calculateDiscount(
            products: List<CartProduct>,
            shippingFee: Int,
            now: LocalTime,
        ): Int {
            val totalPrice = products.sumOf { it.totalPrice }
            return if (totalPrice >= minimumAmount) shippingFee else 0
        }
    }

    data class Percentage(
        val discountPercentage: Int,
        val startTime: LocalTime,
        val endTime: LocalTime,
    ) : Discount() {
        override fun calculateDiscount(
            products: List<CartProduct>,
            shippingFee: Int,
            now: LocalTime,
        ): Int {
            if (now !in startTime..endTime) return 0
            val totalPrice = products.sumOf { it.totalPrice }
            return (totalPrice * discountPercentage) / 100
        }
    }
}
