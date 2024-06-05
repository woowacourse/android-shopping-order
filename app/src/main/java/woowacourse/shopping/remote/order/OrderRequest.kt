package woowacourse.shopping.remote.order

data class OrderRequest(
    val cartItemIds: List<Long>,
)
