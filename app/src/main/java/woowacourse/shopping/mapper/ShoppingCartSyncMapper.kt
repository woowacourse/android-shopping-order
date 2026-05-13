package woowacourse.shopping.mapper

import woowacourse.shopping.backend.retrofit.dto.CartQuantity
import woowacourse.shopping.backend.retrofit.dto.CartRequest
import woowacourse.shopping.backend.retrofit.dto.Content
import woowacourse.shopping.backend.retrofit.dto.OrderInfo
import woowacourse.shopping.backend.retrofit.dto.ShoppingCartResponse
import woowacourse.shopping.model.ShoppingCartItem
import woowacourse.shopping.model.ShoppingItem

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
