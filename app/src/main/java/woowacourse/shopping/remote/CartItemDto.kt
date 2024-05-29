package woowacourse.shopping.remote

data class CartItemDto(
    val id: Long,
    val quantity: Int,
    val product: ProductDto,
)
