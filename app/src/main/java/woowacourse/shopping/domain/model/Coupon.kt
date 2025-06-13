package woowacourse.shopping.domain.model

import java.time.LocalTime

sealed class Coupon(
    open val id: Long,
    open val code: String,
    open val description: String,
    open val expirationDate: String,
) {
    abstract fun isAvailable(
        products: Products,
        now: LocalTime = LocalTime.now(),
    ): Boolean

    data class FixedDiscount(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val discount: Int,
        val minimumOrderAmount: Int,
    ) : Coupon(id, code, description, expirationDate) {
        override fun isAvailable(
            products: Products,
            now: LocalTime,
        ): Boolean = products.getSelectedCartProductsPrice() >= minimumOrderAmount
    }

    data class BuyXGetYFree(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : Coupon(id, code, description, expirationDate) {
        override fun isAvailable(
            products: Products,
            now: LocalTime,
        ): Boolean =
            products.products
                .groupBy { it.productDetail.id }
                .any { (_, group) -> group.sumOf { it.quantity } >= buyQuantity + getQuantity }
    }

    data class FreeShippingOver(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val minimumOrderAmount: Int,
    ) : Coupon(id, code, description, expirationDate) {
        override fun isAvailable(
            products: Products,
            now: LocalTime,
        ): Boolean = products.getSelectedCartProductsPrice() >= minimumOrderAmount
    }

    data class PercentDiscount(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val discount: Int,
        val availableTime: AvailableTime,
    ) : Coupon(id, code, description, expirationDate) {
        override fun isAvailable(
            products: Products,
            now: LocalTime,
        ): Boolean = now in availableTime.start..availableTime.end
    }
}
