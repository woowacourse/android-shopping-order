package woowacourse.shopping.network.dto

import kotlinx.serialization.Serializable
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Product

@Serializable
data class ProductResponse(
    val id: Long,
    val name: String,
    val price: Long,
    val imageUrl: String,
    val category: String
)

fun ProductResponse.toDomain(): Product {
    return Product(
        id = id,
        name = name,
        price = Money(price),
        imageUrl = imageUrl,
        category = category,
    )
}
