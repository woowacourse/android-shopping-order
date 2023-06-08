package woowacourse.shopping.model.data.dto

import com.google.gson.annotations.SerializedName

data class CartItemIdDTO(
    @SerializedName("cartItemId")
    val cartItemId: Long
)
