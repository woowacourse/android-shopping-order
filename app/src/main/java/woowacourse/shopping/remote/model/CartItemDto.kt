package woowacourse.shopping.remote.model

data class CartItemDto(
    val id: Long,
    val quantity: Int,
    val product: ProductDto,
)
