package woowacourse.shopping.data.model

import com.google.gson.annotations.SerializedName

data class CartItem(
    @SerializedName("id") val cartItemId: Int,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("product") val product: Product,
)
