package woowacourse.shopping.data.model

data class CartEntity2(
    val id: Long,
    val quantity: Int,
    val product: ProductEntity,
)
