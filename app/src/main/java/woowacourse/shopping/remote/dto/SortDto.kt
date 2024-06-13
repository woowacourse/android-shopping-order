package woowacourse.shopping.remote.dto

import com.google.gson.annotations.SerializedName

data class SortDto(
    @SerializedName("empty")
    val empty: Boolean,
    @SerializedName("sorted")
    val sorted: Boolean,
    @SerializedName("unsorted")
    val unsorted: Boolean,
)
