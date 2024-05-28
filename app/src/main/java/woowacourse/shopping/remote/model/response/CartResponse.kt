package woowacourse.shopping.remote.model.response

import com.google.gson.annotations.SerializedName

data class CartResponse(
    val id: Int,
    val quantity: Int,
    @SerializedName("product") val productResponse: ProductResponse,
)
