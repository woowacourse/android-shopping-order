package woowacourse.shopping.model.order

data class OrderRequest(
    val cartItemIds: List<Long>,
    val finalPrice: Int
)
