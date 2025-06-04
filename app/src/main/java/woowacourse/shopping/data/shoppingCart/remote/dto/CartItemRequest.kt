package woowacourse.shopping.data.shoppingCart.remote.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CartItemRequest(
    @SerializedName("productId")
    val productId: Long,
    @SerializedName("quantity")
    val quantity: Int,
)
