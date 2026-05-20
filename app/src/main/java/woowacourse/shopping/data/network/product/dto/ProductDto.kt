package woowacourse.shopping.data.network.product.dto

import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.Money
import woowacourse.shopping.domain.Product

@Serializable
class ProductDto(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String,
) {
    fun toDomain(): Product =
        Product(
            name = this.name,
            price = Money(this.price),
            imageUrl = this.imageUrl,
            id = this.id,
            category = this.category,
        )
}
