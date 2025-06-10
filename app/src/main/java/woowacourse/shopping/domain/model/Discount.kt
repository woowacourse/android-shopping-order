package woowacourse.shopping.domain.model

import java.time.LocalTime

sealed class Discount {
    abstract fun isApplicable(
        products: CartProducts,
        now: LocalTime,
    ): Boolean

    abstract fun calculateDiscount(
        products: CartProducts,
        shippingFee: Int,
    ): Int

    data class FixedAmount(
        val discountAmount: Int,
        val minimumAmount: Int,
    ) : Discount() {
        override fun isApplicable(
            products: CartProducts,
            now: LocalTime,
        ): Boolean = products.totalPrice >= minimumAmount

        override fun calculateDiscount(
            products: CartProducts,
            shippingFee: Int,
        ): Int = discountAmount
    }

    data class BuyXGetYFree(
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : Discount() {
        private val minimumQuantity = buyQuantity + getQuantity

        override fun isApplicable(
            products: CartProducts,
            now: LocalTime,
        ): Boolean = products.value.any { it.quantity >= minimumQuantity }

        override fun calculateDiscount(
            products: CartProducts,
            shippingFee: Int,
        ): Int {
            val freeProduct =
                products.value
                    .filter { it.quantity >= minimumQuantity }
                    .maxByOrNull { it.product.price }
            return freeProduct?.let { it.product.price * getQuantity } ?: DEFAULT_DISCOUNT_AMOUNT
        }
    }

    data class FreeShipping(
        val minimumAmount: Int,
    ) : Discount() {
        override fun isApplicable(
            products: CartProducts,
            now: LocalTime,
        ): Boolean = products.totalPrice >= minimumAmount

        override fun calculateDiscount(
            products: CartProducts,
            shippingFee: Int,
        ): Int = shippingFee
    }

    data class Percentage(
        val discountPercentage: Int,
        val startTime: LocalTime,
        val endTime: LocalTime,
    ) : Discount() {
        override fun isApplicable(
            products: CartProducts,
            now: LocalTime,
        ): Boolean = now in startTime..endTime

        override fun calculateDiscount(
            products: CartProducts,
            shippingFee: Int,
        ): Int = (products.totalPrice * discountPercentage) / 100
    }

    companion object {
        private const val DEFAULT_DISCOUNT_AMOUNT = 0
    }
}
