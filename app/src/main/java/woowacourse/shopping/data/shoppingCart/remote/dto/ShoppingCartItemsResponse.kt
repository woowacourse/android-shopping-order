package woowacourse.shopping.data.shoppingCart.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.data.product.remote.dto.ProductResponse
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct

@Serializable
data class ShoppingCartItemsResponse(
    @SerialName("content")
    val shoppingCartItems: List<ShoppingCartItemResponseDto>,
    @SerialName("last")
    val last: Boolean,
) {
    @Serializable
    data class ShoppingCartItemResponseDto(
        @SerialName("id")
        val id: Long,
        @SerialName("product")
        val product: ProductResponse,
        @SerialName("quantity")
        val quantity: Int,
    ) {
        fun toDomain(): ShoppingCartProduct =
            ShoppingCartProduct(
                id = id,
                product = product.toDomain(),
                quantity = quantity,
            )
    }
}
