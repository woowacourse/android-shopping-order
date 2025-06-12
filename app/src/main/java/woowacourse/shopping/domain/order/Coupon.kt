package woowacourse.shopping.domain.order

import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import java.time.LocalDate
import java.time.LocalTime

sealed interface Coupon {
    val id: Int
    val code: String
    val description: String
    val expirationDate: LocalDate
    val discountType: DiscountType
    val isExpiration: Boolean
        get() = expirationDate.isBefore(LocalDate.now())

    data class PriceDiscount(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val discount: Int,
        val minimumAmount: Int,
    ) : Coupon {
        override val discountType: DiscountType = DiscountType.PRICE_DISCOUNT

        fun isAvailable(priceToOrder: Int): Boolean = priceToOrder >= minimumAmount

        fun calculateDiscount(): Int = -discount
    }

    data class Bonus(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : Coupon {
        override val discountType: DiscountType = DiscountType.BONUS

        fun isAvailable(productsToOrder: List<ShoppingCartProduct>): Boolean =
            productsToOrder.any { it.quantity >= buyQuantity + getQuantity }

        fun calculateDiscount(productsToOrder: List<ShoppingCartProduct>): Int = -(calculateMaxPrice(productsToOrder))

        private fun calculateMaxPrice(productsToOrder: List<ShoppingCartProduct>): Int {
            val productsToApplyBonusCoupon =
                productsToOrder.filter { it.quantity >= buyQuantity + getQuantity }

            val maxPricedProduct: ShoppingCartProduct =
                productsToApplyBonusCoupon.maxByOrNull { it.price }
                    ?: throw error("2+1 쿠폰을 적용할 수 없습니다.")

            return maxPricedProduct.product.price
        }
    }

    data class FreeShipping(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val minimumAmount: Int,
    ) : Coupon {
        override val discountType: DiscountType = DiscountType.FREE_SHIPPING

        fun isAvailable(priceToOrder: Int): Boolean = priceToOrder >= minimumAmount

        fun calculateDiscount(shippingFee: ShippingFee): Int = -(shippingFee.amount)
    }

    data class PercentageDiscount(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: LocalDate,
        val discountPercentage: Int,
        val availableStartTime: LocalTime,
        val availableEndTime: LocalTime,
    ) : Coupon {
        override val discountType: DiscountType = DiscountType.PERCENTAGE_DISCOUNT
        override val isExpiration: Boolean
            get() = expirationDate.isBefore(LocalDate.now())

        fun isAvailable(currentTime: LocalTime): Boolean = currentTime in availableStartTime..availableEndTime

        fun calculateDiscount(priceToOrder: Int): Int = -priceToOrder * discountPercentage / PERCENT_BASE
    }

    companion object {
        private const val PERCENT_BASE = 100
    }
}
