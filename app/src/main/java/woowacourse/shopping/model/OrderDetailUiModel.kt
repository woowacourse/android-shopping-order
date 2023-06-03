package woowacourse.shopping.model

import com.example.domain.model.OrderDetail

data class OrderDetailUiModel(
    val orderId: Int,
    val orderAt: String,
    val orderStatus: String,
    val payAmount: Int,
    val usedPoint: Int,
    val savedPoint: Int,
    val orderProducts: List<OrderProductUiModel>
)

fun OrderDetail.toPresentation() = OrderDetailUiModel(
    orderId,
    orderAt,
    orderStatus.toPresentation(),
    payAmount,
    usedPoint,
    savedPoint,
    orderProducts.map { it.toPresentation() }
)
