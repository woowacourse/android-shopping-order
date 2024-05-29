package woowacourse.shopping.data.model.remote

data class CartDto(
    val id: Int,
    val quantity: Int,
    val productDto: ProductDto,
)
