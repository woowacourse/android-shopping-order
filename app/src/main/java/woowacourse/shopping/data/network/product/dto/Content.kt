package woowacourse.shopping.data.network.product.dto

import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.Money
import woowacourse.shopping.domain.Product

@Serializable
data class Content(
    val category: String,
    val id: Long,
    val imageUrl: String,
    val name: String,
    val price: Int,
) {
    fun toDomain(): Product =
        Product(
            name = this.name,
            price = Money(this.price),
            imageUrl = this.imageUrl,
            id = this.id,
        )
}
