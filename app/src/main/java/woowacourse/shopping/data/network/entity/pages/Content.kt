package woowacourse.shopping.data.network.entity.pages

import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.product.Price
import woowacourse.shopping.domain.product.Product

@Serializable
data class Content(
    val category: String,
    val id: Long,
    val imageUrl: String,
    val name: String,
    val price: Int,
) {
    fun toDomain(): Product {
        return Product(id, name, imageUrl, category, Price(price))
    }
}
