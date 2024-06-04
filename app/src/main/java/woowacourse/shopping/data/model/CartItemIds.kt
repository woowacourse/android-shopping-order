package woowacourse.shopping.data.model

import com.google.gson.annotations.SerializedName

data class CartItemIds(
    @SerializedName("cartItemIds") val ids: List<Int>,
)
