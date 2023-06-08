package woowacourse.shopping.presentation.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class OrderModel(
    val orderId: Long,
    val orderDateTime: LocalDateTime,
    val orderProductModels: List<OrderProductModel>,
    val totalPrice: Int,
) {
    fun getOrderDateTimeFormat(): String =
        orderDateTime.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT))

    fun getProductsCombineName() =
        orderProductModels.joinToString(", ") { it.productModel.name }

    fun getTotalProductsCount() = orderProductModels.sumOf { it.quantity }

    companion object {
        const val DATE_TIME_FORMAT = "yyyy년 MM월 dd일 hh:mm:ss"
    }
}
