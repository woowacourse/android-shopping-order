package woowacourse.shopping.data.shoppingCart.remote.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CartItemQuantityRequest(
    @SerializedName("quantity")
    val quantity: Int,
)
