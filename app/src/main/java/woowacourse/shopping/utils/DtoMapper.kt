package woowacourse.shopping.utils

import woowacourse.shopping.data.remote.dto.cart.CartItemDto
import woowacourse.shopping.data.remote.dto.cart.CartItemQuantityDto
import woowacourse.shopping.data.remote.dto.cart.CartItemResponse
import woowacourse.shopping.data.remote.dto.product.ProductDto
import woowacourse.shopping.data.remote.dto.product.ProductResponse
import woowacourse.shopping.domain.model.cart.CartItem
import woowacourse.shopping.domain.model.cart.CartItemCounter
import woowacourse.shopping.domain.model.selector.ItemSelector
import woowacourse.shopping.domain.model.product.Product

object DtoMapper {
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

    fun ProductResponse.toProducts(): List<Product> {
        return productDto.map { it.toProduct() }
    }

    fun ProductDto.toProduct(quantity: Int = 0): Product {
        return Product(
            id = id.toLong(),
            name = name,
            imageUrl = imageUrl,
            price = price,
            category = category,
            cartItemCounter = CartItemCounter(quantity),
            itemSelector = ItemSelector(),
        )
    }
}
