package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.remote.retrofit.dto.CartQuantity
import woowacourse.shopping.data.remote.retrofit.dto.Content
import woowacourse.shopping.data.remote.retrofit.dto.ShoppingCartResponse
import woowacourse.shopping.domain.model.ShoppingCartItem
import woowacourse.shopping.domain.model.ShoppingItem

fun Int.toCartQuantity(): CartQuantity = CartQuantity(quantity = this)

fun Content.toDomainShoppingCartItem(): ShoppingCartItem =
    ShoppingCartItem(
        id = id,
        shoppingItem =
            ShoppingItem(
                product = product.toDomainProduct(),
                quantity = quantity,
            ),
    )

fun ShoppingCartResponse.toDomainShoppingCartItems(): List<ShoppingCartItem> = content.map { item -> item.toDomainShoppingCartItem() }
