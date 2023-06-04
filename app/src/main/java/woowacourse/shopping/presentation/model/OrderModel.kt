package woowacourse.shopping.presentation.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class OrderModel(
    val orderId: Long,
    val orderDateTime: LocalDateTime,
    val orderProductModels: List<OrderProductModel>,
    val totalPrice: Int,
) {
    fun getOrderDateTimeFormat() =
        orderDateTime.format(DateTimeFormatter.ofPattern("yyyy년 mm월 dd일"))

    fun getProductsCombineName() =
        orderProductModels.joinToString(", ") { it.productModel.name }

    fun getTotalProductsCount() = orderProductModels.sumOf { it.quantity }
}
