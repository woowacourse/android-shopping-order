package woowacourse.shopping.data.product.dto

import kotlinx.serialization.Serializable
import woowacourse.shopping.model.Price
import woowacourse.shopping.model.Product

@Serializable
data class Product(
    val id: Long,
    val imageUrl: String,
    val name: String,
    val price: Int,
) {
    fun toDomainProduct(): Product {
        return Product(
            id = id,
            imageUrl = imageUrl,
            name = name,
            price = Price(price),
        )
    }
}
