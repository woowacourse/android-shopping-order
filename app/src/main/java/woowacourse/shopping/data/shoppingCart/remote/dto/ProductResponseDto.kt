package woowacourse.shopping.data.shoppingCart.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.product.Product

@Serializable
data class ProductResponseDto(
    @SerialName("category")
    val category: String,
    @SerialName("id")
    val id: Long,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("name")
    val name: String,
    @SerialName("price")
    val price: Int,
) {
    fun toDomain(): Product =
        Product(
            id = id,
            name = name,
            price = price,
            imageUrl = imageUrl,
            category = category,
        )
}
