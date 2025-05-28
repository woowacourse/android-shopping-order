package woowacourse.shopping.data.product.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductsResponse(
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
    val loadable: Boolean =
        run {
            val pageNumber = pageable?.pageNumber
            val totalPages = this.totalPages

            if (pageNumber == null || totalPages == null) {
                false
            } else {
                pageNumber + 1 < totalPages
            }
        }
}
