package woowacourse.shopping.data.network.response.products

import kotlinx.serialization.Serializable
import woowacourse.shopping.data.network.response.BaseResponse
import woowacourse.shopping.domain.product.Price
import woowacourse.shopping.domain.product.Product

@Serializable
data class ProductResponse(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String,
) : BaseResponse<Product> {
    override fun toDomain(): Product {
        return Product(
            id = id,
            name = name,
            imgUrl = imageUrl,
            category = category,
            price = Price(price),
        )
    }
}
