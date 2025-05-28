package woowacourse.shopping.data.model

data class CartRequest(
    val productId: Long,
    val quantity: Int,
)
