package woowacourse.shopping.ui.model

import java.io.Serializable

data class OrderUiModel(
    val orderId: Long,
    val orderDate: String?,
    val uiOrderProducts: List<OrderProductUiModel>,
    val totalPrice: Long,
    val usedPoint: Long,
    val earnedPoint: Long,
) : Serializable
