package woowacourse.shopping.data.remote.dto

import kotlinx.serialization.Serializable
import woowacourse.shopping.data.model.Money
import woowacourse.shopping.data.model.Product

@Serializable
data class ProductResponse(
    val id: Long,
    val name: String,
    val price: Long,
    val imageUrl: String,
    val category: String,
)

fun ProductResponse.toDomain(): Product =
    Product(
        id = id,
        name = name,
        price = Money(price),
        imageUrl = imageUrl,
        category = category,
    )
