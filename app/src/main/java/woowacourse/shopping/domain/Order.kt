package woowacourse.shopping.domain

import java.time.LocalDateTime

data class Order(
    val purchaseProducts: List<PurchaseProduct>,
    val shippingFee: Int,
    val currentTime: LocalDateTime,
    val isRemoteArea: Boolean
) {
    val totalProductPrice: Int = purchaseProducts.sumOf { it.totalPrice() }
}