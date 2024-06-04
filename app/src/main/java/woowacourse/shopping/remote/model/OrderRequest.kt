package woowacourse.shopping.remote.model

data class OrderRequest(
    val cartItemIds: List<Long>
)