package woowacourse.shopping.remote.model.response

import com.squareup.moshi.Json

data class CartsResponse(
    @Json(name = "totalPages") val totalPages: Int,
    @Json(name = "totalElements") val totalElements: Int,
    @Json(name = "sort") val sortResponse: SortResponse,
    @Json(name = "first") val first: Boolean,
    @Json(name = "last") val last: Boolean,
    @Json(name = "pageable") val pageableResponse: PageableResponse,
    @Json(name = "number") val number: Int,
    @Json(name = "numberOfElements") val numberOfElements: Int,
    @Json(name = "size") val size: Int,
    @field:Json(name = "content") val content: List<CartResponse>,
    @Json(name = "empty") val empty: Boolean,
)
