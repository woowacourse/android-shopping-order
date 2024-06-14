package woowacourse.shopping.data.remote.dto.cart

import com.google.gson.annotations.SerializedName

data class CartOrderRequest(
    @SerializedName("cartItemIds")
    val cartItemIds: List<Long>,
)
