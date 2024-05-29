package woowacourse.shopping.remote.model.request

data class PostOrderRequest(
    val cartItemsIds: List<Int>,
)
