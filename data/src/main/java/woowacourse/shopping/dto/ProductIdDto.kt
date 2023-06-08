package woowacourse.shopping.dto

import com.google.gson.annotations.SerializedName

data class ProductIdDto(
    @SerializedName("productId") val productId: Int = 0
)
