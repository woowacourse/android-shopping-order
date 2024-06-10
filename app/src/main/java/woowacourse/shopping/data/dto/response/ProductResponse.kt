package woowacourse.shopping.data.dto.response

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @SerializedName("content") val productItemResponse: List<ProductItemResponse>,
    val pageable: PageableResponse,
    val last: Boolean,
    val totalPages: Int,
    val totalElements: Int,
    val sort: SortResponse,
    val first: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val size: Int,
    val empty: Boolean,
)
