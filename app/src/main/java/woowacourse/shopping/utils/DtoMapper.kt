package woowacourse.shopping.utils

import woowacourse.shopping.data.remote.dto.cart.CartItemDto
import woowacourse.shopping.data.remote.dto.cart.CartItemQuantityDto
import woowacourse.shopping.data.remote.dto.cart.CartItemResponse
import woowacourse.shopping.data.remote.dto.product.ProductDto
import woowacourse.shopping.data.remote.dto.product.ProductResponse
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product

object DtoMapper {

    fun CartItemResponse.toCartItemList(): List<CartItem>{
        return cartItemDto.map { it.toCartItem() }
    }

    fun CartItemDto.toCartItem(): CartItem {
        return CartItem(
            id = id.toLong(),
            product = product.toProduct(),
        )
    }

    fun CartItemQuantityDto.toQuantity(): Int {
        return quantity
    }

    fun ProductResponse.toProductList() :List<Product>{
        return productDto.map { it.toProduct() }
    }

    fun ProductDto.toProduct(): Product {
        return Product(
            id = id.toLong(),
            name = name,
            imageUrl = imageUrl,
            price = price,
            category = category,
        )
    }

}
