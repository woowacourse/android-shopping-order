package woowacourse.shopping.model

import woowacourse.shopping.domain.model.Order
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Order.toUiModel(): OrderDetailModel {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return OrderDetailModel(
        orderId,
        LocalDateTime.parse(orderedDateTime, formatter),
        products.map { OrderDetailProductModel.of(it) },
        totalPrice,
    )
}
