package woowacourse.shopping.data.carts.dto

import com.google.gson.annotations.SerializedName
import woowacourse.shopping.data.goods.dto.Content

data class CartContent(
    @SerializedName("id")
    val id: Int,
    @SerializedName("product")
    val product: Content,
    @SerializedName("quantity")
    val quantity: Int,
)
