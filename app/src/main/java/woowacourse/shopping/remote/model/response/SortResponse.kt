package woowacourse.shopping.remote.model.response

import com.squareup.moshi.Json

data class SortResponse(
    @Json(name = "sorted") val sorted: Boolean,
    @Json(name = "unsorted") val unSorted: Boolean,
    @Json(name = "empty") val empty: Boolean,
)
