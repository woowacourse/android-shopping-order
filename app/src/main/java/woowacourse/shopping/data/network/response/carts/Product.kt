package woowacourse.shopping.data.network.response.carts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.product.Price
import woowacourse.shopping.domain.product.Product

@Serializable
data class Product(
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
    fun toDomain(): Product {
        return Product(
            id = id,
            name = name,
            imgUrl = imageUrl,
            category = category,
            price = Price(price),
        )
    }
}
