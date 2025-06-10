package woowacourse.shopping.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.ProductDetail

@Serializable
data class ProductDetailResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("price")
    val price: Int,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("category")
    val category: String,
) {
    companion object {
        fun ProductDetailResponse.toDomain(): ProductDetail =
            ProductDetail(
                id = id,
                name = name,
                category = category,
                imageUrl = imageUrl,
                price = price,
            )
    }
}
