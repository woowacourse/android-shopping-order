package woowacourse.shopping.data.model.dto

import com.google.gson.annotations.SerializedName

data class PageableDto(
    @SerializedName("sort") val sort: SortDto,
    @SerializedName("pageNumber") val pageNumber: Int,
    @SerializedName("pageSize") val pageSize: Int,
    @SerializedName("offset") val offset: Int,
    @SerializedName("paged") val paged: Boolean,
    @SerializedName("unpaged") val unpaged: Boolean,
)
