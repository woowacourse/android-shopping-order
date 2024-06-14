package woowacourse.shopping.data.order.remote.dto

data class OrderRequest(
    val cartItemIds: List<Long>,
)
