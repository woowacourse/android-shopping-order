package woowacourse.shopping.data.network.product.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.Money
import woowacourse.shopping.domain.Product

@Serializable
data class ProductDto(
    @SerialName("category")
    val category: String,
    @SerialName("id")
    val id: Int,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("name")
    val name: String,
    @SerialName("price")
    val price: Int,
) {
    fun toDomain(): Product {
        return Product(
            name = this.name,
            price = Money(this.price),
            imageUrl = this.imageUrl,
            id = this.id.toString(),
            category = this.category,
        )
    }
}
