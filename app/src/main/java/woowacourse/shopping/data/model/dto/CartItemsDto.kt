package woowacourse.shopping.data.model.dto

import com.google.gson.annotations.SerializedName

data class CartItemsDto(
    @SerializedName("cartItemIds") val cartItemIds: List<Long>,
)
