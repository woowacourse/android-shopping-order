package woowacourse.shopping.model.order

data class OrderRequest(
    val cartItemIds: List<Int>,
    val finalPrice: Int
)
