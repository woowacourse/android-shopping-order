package woowacourse.shopping.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductEntity(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
) {
    companion object {
        val errorData: ProductEntity = ProductEntity(-1L, "", 0, "")
    }
}
