package woowacourse.shopping.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import woowacourse.shopping.domain.model.CartProduct

@Serializable
data class CartProductDto(
    @SerialName("id")
    val id: Int,
    @SerialName("quantity")
    val quantity: Int,
    @SerialName("product")
    val product: ProductDto,
) {
    fun toCartProduct(): CartProduct =
        CartProduct(
            id = id,
            quantity = quantity,
            product = product.toProduct(),
        )
}
