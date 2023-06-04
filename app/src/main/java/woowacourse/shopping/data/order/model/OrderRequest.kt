package woowacourse.shopping.data.order.model

data class OrderRequest(
    val cartItemIds: List<Long>,
    val finalPrice: Int
)
