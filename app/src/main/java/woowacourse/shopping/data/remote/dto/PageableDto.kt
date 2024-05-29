package woowacourse.shopping.data.remote.dto


import com.google.gson.annotations.SerializedName

data class PageableDto(
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("pageNumber")
    val pageNumber: Int,
    @SerializedName("pageSize")
    val pageSize: Int,
    @SerializedName("paged")
    val paged: Boolean,
    @SerializedName("sort")
    val sort: SortDto,
    @SerializedName("unpaged")
    val unpaged: Boolean
)
