package woowacourse.shopping.model

data class CartProductResponse(
    val id: Long,
    val product: ProductResponse,
    val quantity: Int,
)
