package woowacourse.shopping.remote.model.response

import com.google.gson.annotations.SerializedName

data class SortResponse(
    val sorted: Boolean,
    @SerializedName("unsorted") val unSorted: Boolean,
    val empty: Boolean,
)
