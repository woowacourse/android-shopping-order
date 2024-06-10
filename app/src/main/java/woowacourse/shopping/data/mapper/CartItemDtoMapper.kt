package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.remote.dto.cart.CartItemDto
import woowacourse.shopping.data.remote.dto.cart.CartItemQuantityDto
import woowacourse.shopping.data.remote.dto.cart.CartItemResponse
import woowacourse.shopping.domain.model.cart.CartItem
import woowacourse.shopping.data.mapper.ProductDtoMapper.toProduct

object CartItemDtoMapper {
    fun CartItemResponse.toCartItems(): List<CartItem> {
        return cartItemDto.map { it.toCartItem() }
    }

    private fun CartItemDto.toCartItem(): CartItem {
        return CartItem(
            id = id.toLong(),
            product = product.toProduct(quantity),
        )
    }

    fun CartItemQuantityDto.toQuantity(): Int {
        return quantity
    }
}
