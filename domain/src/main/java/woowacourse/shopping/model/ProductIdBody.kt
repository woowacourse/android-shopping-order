package woowacourse.shopping.model

import com.google.gson.annotations.SerializedName

data class ProductIdBody(
    @SerializedName("productId") val productId: Int = 0
)
