package woowacourse.shopping.data.remote.product

import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.common.Money
import woowacourse.shopping.domain.model.product.Product

@Serializable
data class ProductResponseDto(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String,
) {
    fun toDomain(): Product =
        Product(
            id = (id),
            name = name,
            price = Money(price),
            imageUrl = imageUrl,
            category = category,
        )
}
