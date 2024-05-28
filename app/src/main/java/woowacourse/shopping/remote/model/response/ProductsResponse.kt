package woowacourse.shopping.remote.model.response

import com.google.gson.annotations.SerializedName

data class ProductsResponse(
    val content: List<ProductResponse>,
    @SerializedName("pageable") val pageableResponse: PageableResponse,
    val last: Boolean,
    val totalPages: Int,
    val totalElements: Int,
    @SerializedName("sort") val sortResponse: SortResponse,
    val first: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val size: Int,
    val empty: Boolean,
)
