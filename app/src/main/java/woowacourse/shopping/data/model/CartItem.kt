package woowacourse.shopping.data.model

import com.google.gson.annotations.SerializedName

data class CartItem(
    @SerializedName("id") val cartItemId: Int,
    val quantity: Int,
    val product: Product,
)
