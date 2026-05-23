package woowacourse.shopping.domain

import woowacourse.shopping.domain.coupon.Discount
import java.time.LocalDateTime

data class Order(
    val purchaseProducts: List<PurchaseProduct>,
    val shippingFee: Int = 3000,
    val currentTime: LocalDateTime,
    val isRemoteArea: Boolean
) {
    val totalProductPrice: Int = purchaseProducts.sumOf { it.totalPrice() }

    fun calculateFinalPrice(discount: Discount): Int {
        return (totalProductPrice + shippingFee) - discount.totalAmount
    }

    fun getAllIds(): List<Long> = purchaseProducts.map { it.id }
}