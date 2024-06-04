package woowacourse.shopping.data.datasource.remote.model.request

data class AddCartItemRequest(
    val productId: Int,
    val quantity: Int,
)
