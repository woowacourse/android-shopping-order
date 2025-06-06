package woowacourse.shopping.data.cart.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CartResponse(
    @SerialName("content")
    val content: List<Content>?,
    @SerialName("empty")
    val empty: Boolean?,
    @SerialName("first")
    val first: Boolean?,
    @SerialName("last")
    val last: Boolean?,
    @SerialName("number")
    val number: Int?,
    @SerialName("numberOfElements")
    val numberOfElements: Int?,
    @SerialName("pageable")
    val pageable: Pageable?,
    @SerialName("size")
    val size: Int?,
    @SerialName("sort")
    val sort: Sort?,
    @SerialName("totalElements")
    val totalElements: Long?,
    @SerialName("totalPages")
    val totalPages: Int?,
) {
    @Serializable
    data class Content(
        @SerialName("id")
        val id: Long?,
        @SerialName("product")
        val product: Product?,
        @SerialName("quantity")
        val quantity: Int?,
    ) {
        @Serializable
        data class Product(
            @SerialName("category")
            val category: String?,
            @SerialName("id")
            val id: Long?,
            @SerialName("imageUrl")
            val imageUrl: String?,
            @SerialName("name")
            val name: String?,
            @SerialName("price")
            val price: Int?,
        )
    }

    @Serializable
    data class Pageable(
        @SerialName("offset")
        val offset: Long?,
        @SerialName("pageNumber")
        val pageNumber: Int?,
        @SerialName("pageSize")
        val pageSize: Int?,
        @SerialName("paged")
        val paged: Boolean?,
        @SerialName("sort")
        val sort: Sort?,
        @SerialName("unpaged")
        val unPaged: Boolean?,
    )

    @Serializable
    data class Sort(
        @SerialName("empty")
        val empty: Boolean?,
        @SerialName("sorted")
        val sorted: Boolean?,
        @SerialName("unsorted")
        val unsorted: Boolean?,
    )
}
