package woowacourse.shopping.data.model

import com.google.gson.annotations.SerializedName

data class CartRemoteEntity(
    val id: Long,
    val quantity: Int,
    @SerializedName("product")
    val productEntity: ProductEntity,
) {
    companion object {
        val errorData: CartRemoteEntity = CartRemoteEntity(-1L, -1, ProductEntity.errorData)
    }
}
