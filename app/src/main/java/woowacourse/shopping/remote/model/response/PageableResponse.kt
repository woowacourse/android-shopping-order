package woowacourse.shopping.remote.model.response

import com.google.gson.annotations.SerializedName

data class PageableResponse(
    @SerializedName("sort") val sortResponse: SortResponse,
    val pageNumber: Int,
    val pageSize: Int,
    val offset: Int,
    val paged: Boolean,
    @SerializedName("unpaged") val unPaged: Boolean,
)
