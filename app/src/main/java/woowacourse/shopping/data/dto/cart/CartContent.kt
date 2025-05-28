package woowacourse.shopping.data.dto.cart

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.CartItem

@Serializable
data class CartContent(
    @SerialName("id")
    val id: Long,
    @SerialName("product")
    val cartProduct: CartResponse,
    @SerialName("quantity")
    val quantity: Int,
)

fun CartContent.toDomain(): CartItem =
    CartItem(
        cartId = this.id,
        product = this.cartProduct.toDomain(),
        quantity = this.quantity,
    )
