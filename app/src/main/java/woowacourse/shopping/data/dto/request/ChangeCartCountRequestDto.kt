package woowacourse.shopping.data.dto.request

import com.google.gson.annotations.SerializedName

data class ChangeCartCountRequestDto(
    @SerializedName("quantity")
    val quantity: Int,
)
