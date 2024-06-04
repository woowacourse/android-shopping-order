package woowacourse.shopping.remote.model.response

data class CartResponse(
    val id: Int,
    val quantity: Int,
    val product: ProductResponse,
)
