package woowacourse.shopping.data.model

data class CartRemoteEntity(
    val id: Long,
    val quantity: Int,
    val product: ProductEntity,
)
