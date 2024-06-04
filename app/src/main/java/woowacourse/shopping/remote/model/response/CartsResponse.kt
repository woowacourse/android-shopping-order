package woowacourse.shopping.remote.model.response

import com.google.gson.annotations.SerializedName

data class CartsResponse(
    val totalPages: Int,
    val totalElements: Int,
    @SerializedName("sort") val sortResponse: SortResponse,
    val first: Boolean,
    val last: Boolean,
    @SerializedName("pageable") val pageableResponse: PageableResponse,
    val number: Int,
    val numberOfElements: Int,
    val size: Int,
    val content: List<CartResponse>,
    val empty: Boolean,
)
