package woowacourse.shopping.remote.model.response

data class CartItemResponse(
    val id: Long,
    val quantity: Int,
    val product: ProductResponse,
)
