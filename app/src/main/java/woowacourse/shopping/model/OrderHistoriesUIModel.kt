package woowacourse.shopping.model

data class OrderHistoriesUIModel(
    val orderHistories: List<OrderHistoryUIModel>,
    val lastOrderId: Long
)
