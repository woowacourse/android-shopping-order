package woowacourse.shopping.domain.mapper

import woowacourse.shopping.data.model.CartItemEntity
import woowacourse.shopping.domain.model.cart.CartItem
import woowacourse.shopping.domain.model.cart.CartItemCounter

object CartItemEntityMapper {
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
