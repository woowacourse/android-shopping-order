package woowacourse.shopping.data.order.model.dto.request

data class OrderRequest(
    val cartItemIds: List<Long>,
    val finalPrice: Int
)
