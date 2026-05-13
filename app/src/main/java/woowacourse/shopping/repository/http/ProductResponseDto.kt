package woowacourse.shopping.repository.http

import kotlinx.serialization.Serializable
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductId

@Serializable
data class ProductResponseDto(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
) {
    fun toDomain(): Product =
        Product(
            id = ProductId.fromRemoteId(id),
            name = name,
            price = Money(price),
            imageUrl = imageUrl,
        )
}

@Serializable
data class ProductPageResponseDto(
    val content: List<ProductResponseDto>? = null,
    val totalElements: Long? = null,
    val last: Boolean? = null,
)
