package woowacourse.shopping.data.dto.request

import com.google.gson.annotations.SerializedName

data class AddCartRequestDto(
    @SerializedName("productId")
    val productId: Long,
)
