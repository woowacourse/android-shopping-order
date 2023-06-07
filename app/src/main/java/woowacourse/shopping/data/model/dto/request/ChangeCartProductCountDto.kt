package woowacourse.shopping.data.model.dto.request

import com.google.gson.annotations.SerializedName

data class ChangeCartProductCountDto(
    @SerializedName("quantity")
    val count: Int
)
