package woowacourse.shopping.data.model

import com.google.gson.annotations.SerializedName

data class CartQuantity(
    @SerializedName("quantity") val quantity: Int,
)
