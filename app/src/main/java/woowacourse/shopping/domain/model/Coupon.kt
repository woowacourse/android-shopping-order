package woowacourse.shopping.domain.model

import java.time.LocalTime

sealed interface Coupon {
    val id: Long
    val code: String
    val description: String
    val expirationDate: String

    data class FixedDiscount(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val discount: Int,
        val minimumOrderAmount: Int,
    ) : Coupon

    data class BuyXGetYFree(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val buyQuantity: Int,
        val getQuantity: Int,
    ) : Coupon

    data class FreeShippingOver(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val minimumOrderAmount: Int,
    ) : Coupon

    data class PercentDiscount(
        override val id: Long,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        val discount: Int,
        val availableTime: AvailableTime,
    ) : Coupon

    companion object {
        fun Coupon.isAvailable(
            products: Products,
            now: LocalTime = LocalTime.now(),
        ): Boolean {
            val totalPrice = products.getSelectedCartProductsPrice()

            return when (this) {
                is FixedDiscount -> totalPrice >= minimumOrderAmount

                is FreeShippingOver -> totalPrice >= minimumOrderAmount

                is BuyXGetYFree -> {
                    products.products
                        .groupBy { it.productDetail.id }
                        .any { (_, group) -> group.sumOf { it.quantity } >= buyQuantity + getQuantity }
                }

                is PercentDiscount -> {
                    now >= availableTime.start && now <= availableTime.end
                }
            }
        }
    }
}
