package woowacourse.shopping.model

import com.google.gson.annotations.SerializedName

data class QuantityBody(
    @SerializedName("quantity") val quantity: Int = 0
)
