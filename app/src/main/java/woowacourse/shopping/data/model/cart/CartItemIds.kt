package woowacourse.shopping.data.model.cart

import com.google.gson.annotations.SerializedName

data class CartItemIds(
    @SerializedName("cartItemIds") val ids: List<Int>,
)
