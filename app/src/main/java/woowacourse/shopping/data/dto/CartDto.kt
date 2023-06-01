package woowacourse.shopping.data.dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CartGetResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("product")
    val product: ProductGetResponse,
)

@Serializable
data class CartAddRequest(
    @SerializedName("productId")
    val productId: Int,
)

@Serializable
data class CartPatchRequest(
    @SerializedName("quantity")
    val quantity: Int,
)
