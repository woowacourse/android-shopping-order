package woowacourse.shopping.remote.model.request

data class PostOrderRequest(
    val cartItemIds: List<Int>,
)
