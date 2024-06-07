package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.dto.CartItemDto
import woowacourse.shopping.data.dto.CartResponse
import woowacourse.shopping.data.dto.ProductDto
import woowacourse.shopping.data.local.recent.RecentProductEntity
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import java.time.LocalDateTime

fun Product.toRecentProductEntity(): RecentProductEntity {
    return RecentProductEntity(
        productId = productId,
        productName = name,
        imageUrl = imageUrl,
        dateTime = LocalDateTime.now().toString(),
        category = category,
    )
}

fun ProductDto.toProduct(): Product {
    return Product(
        category,
        productId,
        imageUrl,
        name,
        price,
    )
}

fun CartResponse.toCartItems(): List<CartItem> {
    return cartItems.map { cartItem -> cartItem.toCartItem() }
}

fun CartItemDto.toCartItem(): CartItem {
    return CartItem(cartItemId, quantity, productDto.toProduct())
}
