package woowacourse.shopping.domain.model

import woowacourse.shopping.presentation.product.catalog.ProductUiModel

data class OrderInfo(
    val orderProducts: List<ProductUiModel>,
    val discount: Int = DEFAULT_DISCOUNT,
    val deliveryAmount: Int = DEFAULT_DELIVERY_AMOUNT,
) {
    val orderAmount: Int = orderProducts.sumOf { it.price * it.quantity }
    val totalAmount: Int = orderAmount + discount + deliveryAmount

    companion object {
        private const val DEFAULT_DISCOUNT = 0
        private const val DEFAULT_DELIVERY_AMOUNT = 3000
    }
}
