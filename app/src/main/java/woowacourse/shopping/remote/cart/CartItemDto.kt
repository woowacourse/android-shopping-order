package woowacourse.shopping.remote.cart

import woowacourse.shopping.remote.product.ProductDto
import woowacourse.shopping.ui.model.CartItem

data class CartItemDto(
    val id: Long,
    val quantity: Int,
    val product: ProductDto,
) {
    companion object {
        fun CartItemDto.toDomain() =
            CartItem(
                id = id,
                quantity = quantity,
                product = product,
                checked = false,
            )
    }
}
