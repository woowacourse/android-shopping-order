package woowacourse.shopping.remote.model.response

import com.squareup.moshi.Json

data class PageableResponse(
    @Json(name = "sort") val sortResponse: SortResponse,
    @Json(name = "pageNumber") val pageNumber: Int,
    @Json(name = "pageSize") val pageSize: Int,
    @Json(name = "offset")val offset: Int,
    @Json(name = "paged") val paged: Boolean,
    @Json(name = "unpaged") val unPaged: Boolean,
)
