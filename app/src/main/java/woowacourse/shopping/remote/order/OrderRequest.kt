package woowacourse.shopping.remote.order

data class OrderRequest(
    val cartItemsId: List<Long>,
)
