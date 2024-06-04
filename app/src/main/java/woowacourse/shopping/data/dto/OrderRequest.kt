package woowacourse.shopping.data.dto

import com.google.gson.annotations.SerializedName

data class OrderRequest(
    @SerializedName("cartItemIds") val ids: List<Int>,
)
