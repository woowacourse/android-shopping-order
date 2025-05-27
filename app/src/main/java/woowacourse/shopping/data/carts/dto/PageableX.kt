package woowacourse.shopping.data.carts.dto


import com.google.gson.annotations.SerializedName

data class PageableX(
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("pageNumber")
    val pageNumber: Int,
    @SerializedName("pageSize")
    val pageSize: Int,
    @SerializedName("paged")
    val paged: Boolean,
    @SerializedName("sort")
    val sort: SortXX,
    @SerializedName("unpaged")
    val unpaged: Boolean
)