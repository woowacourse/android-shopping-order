package woowacourse.shopping.remote.model.request

data class PostCartItemRequest(
    val productId: Int,
    val quantity: Int,
)
