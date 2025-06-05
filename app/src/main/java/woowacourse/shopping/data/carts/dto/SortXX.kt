package woowacourse.shopping.data.carts.dto

import com.google.gson.annotations.SerializedName

data class SortXX(
    @SerializedName("empty")
    val empty: Boolean,
    @SerializedName("sorted")
    val sorted: Boolean,
    @SerializedName("unsorted")
    val unsorted: Boolean,
)
