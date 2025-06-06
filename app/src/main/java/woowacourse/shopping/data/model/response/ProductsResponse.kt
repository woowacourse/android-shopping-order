package woowacourse.shopping.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductDetail

@Serializable
data class ProductsResponse(
    @SerialName("totalElements")
    val totalElements: Long,
    @SerialName("totalPages")
    val totalPages: Int,
    @SerialName("size")
    val size: Int,
    @SerialName("content")
    val content: List<Content>,
    @SerialName("number")
    val number: Int,
    @SerialName("sort")
    val sort: SortResponse,
    @SerialName("pageable")
    val pageable: PageableResponse,
    @SerialName("first")
    val first: Boolean,
    @SerialName("last")
    val last: Boolean,
    @SerialName("numberOfElements")
    val numberOfElements: Int,
    @SerialName("empty")
    val empty: Boolean,
) {
    @Serializable
    data class Content(
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
            fun Content.toDomain(): Product =
                Product(
                    productDetail =
                        ProductDetail(
                            id = id,
                            name = name,
                            imageUrl = imageUrl,
                            price = price,
                            category = category,
                        ),
                    quantity = 0,
                )
        }
    }
}
