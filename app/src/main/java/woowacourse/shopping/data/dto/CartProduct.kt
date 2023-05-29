package woowacourse.shopping.data.dto

typealias CartProductDto = CartProduct

data class CartProduct(
    val id: Int,
    val product: Product,
    val quantity: Int,
)
