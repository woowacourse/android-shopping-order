package woowacourse.shopping.data.model.dto.request.cart

import com.google.gson.annotations.SerializedName

data class ChangeCartProductCountDto(
    @SerializedName("quantity")
    val count: Int
)
