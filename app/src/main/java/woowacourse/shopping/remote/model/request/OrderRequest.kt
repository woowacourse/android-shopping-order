package woowacourse.shopping.remote.model.request

data class OrderRequest(
    val cartItemIds: List<Long>,
)
