package woowacourse.shopping.data.cart.remote.dto

import woowacourse.shopping.data.product.remote.dto.ProductDto
import woowacourse.shopping.data.product.remote.dto.ProductDto.Companion.toDomain
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.ui.model.CartItemUiModel

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
                product = product.toDomain(),
            )
    }
}
