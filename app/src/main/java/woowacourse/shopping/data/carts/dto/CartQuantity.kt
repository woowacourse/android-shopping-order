package woowacourse.shopping.data.carts.dto


import com.google.gson.annotations.SerializedName

data class CartQuantity(
    @SerializedName("quantity")
    val quantity: Int
)