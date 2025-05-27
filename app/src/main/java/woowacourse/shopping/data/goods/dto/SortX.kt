package woowacourse.shopping.data.goods.dto


import com.google.gson.annotations.SerializedName

data class SortX(
    @SerializedName("empty")
    val empty: Boolean,
    @SerializedName("sorted")
    val sorted: Boolean,
    @SerializedName("unsorted")
    val unsorted: Boolean
)