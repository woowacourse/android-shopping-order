package woowacourse.shopping.data.dto

data class CartGetResponse(
    val id: Int,
    val quantity: Int,
    val product: ProductGetResponse,
)

data class CartAddRequest(
    val productId: Int,
)

data class CartPatchRequest(
    val quantity: Int,
)
