package woowacourse.shopping.utils.Mapper

import woowacourse.shopping.data.model.CartItemEntity
import woowacourse.shopping.data.remote.dto.cart.CartItemDto
import woowacourse.shopping.data.remote.dto.cart.CartItemQuantityDto
import woowacourse.shopping.data.remote.dto.cart.CartItemResponse
import woowacourse.shopping.domain.model.cart.CartItem
import woowacourse.shopping.domain.model.cart.CartItemCounter
import woowacourse.shopping.utils.Mapper.ProductMapper.toProduct

object CartItemMapper {
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

    fun CartItemEntity.toCartItem(): CartItem {
        return CartItem(
            id = id,
            product =
            product.copy(
                id = product.id,
                cartItemCounter = CartItemCounter(count),
                imageUrl = product.imageUrl,
                name = product.name,
                price = product.price,
                category = product.category,
            ),
        )
    }

    fun CartItem.toCartItemEntity(): CartItemEntity {
        return CartItemEntity(product = product)
    }
}
