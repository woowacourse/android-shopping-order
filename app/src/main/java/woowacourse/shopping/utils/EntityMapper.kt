package woowacourse.shopping.utils

import woowacourse.shopping.data.model.CartItemEntity
import woowacourse.shopping.data.model.RecentlyProductEntity
import woowacourse.shopping.domain.model.cart.CartItem
import woowacourse.shopping.domain.model.cart.CartItemCounter
import woowacourse.shopping.domain.model.product.RecentlyProduct

object EntityMapper {
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

    fun RecentlyProductEntity.toRecentlyProduct(): RecentlyProduct {
        return RecentlyProduct(
            id = id,
            productId = productId,
            imageUrl = imageUrl,
            name = name,
            category = category,
        )
    }

    fun RecentlyProduct.toRecentlyProductEntity(): RecentlyProductEntity {
        return RecentlyProductEntity(
            productId = productId,
            name = name,
            imageUrl = imageUrl,
            category = category,
        )
    }
}
