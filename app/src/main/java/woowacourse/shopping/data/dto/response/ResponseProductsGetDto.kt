package woowacourse.shopping.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ResponseProductsGetDto(
    val totalPages: Int,
    val totalElements: Long,
    val sort: Sort,
    val first: Boolean,
    val last: Boolean,
    val pageable: Pageable,
    val number: Int,
    val numberOfElements: Int,
    val size: Int,
    val content: List<Product>,
    val empty: Boolean,
) {
    @Serializable
    data class Product(
        val id: Long,
        val name: String,
        val price: Int,
        val imageUrl: String,
        val category: String,
    )

    @Serializable
    data class Pageable(
        val sort: ResponseCartItemsGetDto.Sort,
        val pageNumber: Int,
        val pageSize: Int,
        val paged: Boolean,
        val unpaged: Boolean,
        val offset: Long,
    )

    @Serializable
    data class Sort(
        val sorted: Boolean,
        val unsorted: Boolean,
        val empty: Boolean,
    )
}
