package woowacourse.shopping.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductDetail

@Serializable
data class CartItemsResponse(
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
        @SerialName("quantity")
        val quantity: Int,
        @SerialName("product")
        val product: Product,
    ) {
        @Serializable
        data class Product(
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
        )

        companion object {
            fun Content.toDomain(): woowacourse.shopping.domain.model.Product =
                Product(
                    productDetail =
                        ProductDetail(
                            id = product.id,
                            name = product.name,
                            imageUrl = product.imageUrl,
                            price = product.price,
                            category = product.category,
                        ),
                    cartId = id,
                    quantity = quantity,
                )
        }
    }
}
