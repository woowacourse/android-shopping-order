package woowacourse.shopping.data.carts.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.data.goods.dto.Content

@Serializable
data class CartContent(
    @SerialName("id")
    val id: Int,
    @SerialName("product")
    val product: Content,
    @SerialName("quantity")
    val quantity: Int,
)
