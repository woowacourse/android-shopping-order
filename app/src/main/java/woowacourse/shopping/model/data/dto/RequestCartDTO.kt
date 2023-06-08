package woowacourse.shopping.model.data.dto

import com.google.gson.annotations.SerializedName

data class RequestCartDTO(
    @SerializedName("productId")
    val productId: Long,
    @SerializedName("quantity")
    val quantity: Int
)
