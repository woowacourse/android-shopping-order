package woowacourse.shopping.data.remote.model.dto

import com.google.gson.annotations.SerializedName

data class SortDto(
    @SerializedName("sorted") val sorted: Boolean,
    @SerializedName("unsorted") val unsorted: Boolean,
    @SerializedName("empty") val empty: Boolean,
)
