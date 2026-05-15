package woowacourse.shopping.data.mapper

import woowacourse.shopping.data.remote.retrofit.dto.CartQuantity
import woowacourse.shopping.data.remote.retrofit.dto.CartRequest
import woowacourse.shopping.data.remote.retrofit.dto.Content
import woowacourse.shopping.data.remote.retrofit.dto.OrderInfo
import woowacourse.shopping.data.remote.retrofit.dto.ShoppingCartResponse
import woowacourse.shopping.domain.model.ShoppingCartItem
import woowacourse.shopping.domain.model.ShoppingItem

fun ShoppingItem.toCartRequest(quantity: Int = getQuantity()): CartRequest =
    CartRequest(
        productId = getProductId(),
        quantity = quantity,
    )

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

fun List<ShoppingCartItem>.toOrderInfo(): OrderInfo =
    OrderInfo(
        cartItemIds = map { shoppingCartItem -> shoppingCartItem.getId() },
    )
