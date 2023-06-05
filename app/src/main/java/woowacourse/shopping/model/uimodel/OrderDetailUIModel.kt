package woowacourse.shopping.model.uimodel

class OrderDetailUIModel(
    val orderItems: List<OrderProductUIModel>,
    val originalPrice: Int,
    val usedPoints: Int,
    val orderPrice: Int
)
