package woowacourse.shopping.data.model.dto.request

import com.google.gson.annotations.SerializedName

data class AddCartProductDto(
    @SerializedName("productId")
    val productId: Long
)
