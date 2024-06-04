package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.db.recent.RecentProductEntity
import woowacourse.shopping.data.dto.CartItemDto
import woowacourse.shopping.data.dto.ProductDto
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.view.order.adapter.cart.ShoppingCartViewItem.CartViewItem
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

fun CartItemDto.toCartItem(): CartItem {
    return CartItem(cartItemId, quantity, productDto.toProduct())
}

fun CartItemDto.toCartViewItem(): CartViewItem {
    return CartViewItem(toCartItem())
}
