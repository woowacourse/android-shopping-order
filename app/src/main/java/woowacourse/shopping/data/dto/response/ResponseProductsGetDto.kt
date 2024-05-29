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
}
