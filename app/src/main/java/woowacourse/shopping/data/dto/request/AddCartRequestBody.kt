package woowacourse.shopping.data.dto.request

data class AddCartRequestBody(
    val productId: Long,
    val quantity: Int,
)

data class UpdateCartRequestBody(
    val quantity: Int,
)
