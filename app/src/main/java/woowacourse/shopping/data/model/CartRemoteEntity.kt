package woowacourse.shopping.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CartRemoteEntity(
    val id: Long,
    val quantity: Int,
    val product: ProductEntity,
) {
    companion object {
        val errorData: CartRemoteEntity = CartRemoteEntity(-1L, -1, ProductEntity.errorData)
    }
}
