package woowacourse.shopping.data.model

data class CartRemoteEntity(
    val id: Long,
    val quantity: Int,
    val productEntity: ProductEntity,
) {
    companion object {
        val errorData: CartRemoteEntity = CartRemoteEntity(-1L, -1, ProductEntity.errorData)
    }
}
