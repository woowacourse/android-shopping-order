package woowacourse.shopping.data.shoppingCart.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct

@Serializable
data class ShoppingCartItemResponseDto(
    @SerialName("id")
    val id: Int,
    @SerialName("product")
    val product: ProductResponseDto,
    @SerialName("quantity")
    val quantity: Int,
) {
    fun toDomain(): ShoppingCartProduct =
        ShoppingCartProduct(
            product = product.toDomain(),
            quantity = quantity,
        )
}
