package woowacourse.shopping.data.shoppingCart.mapper

import woowacourse.shopping.data.shoppingCart.remote.dto.ShoppingCartItemResponseDto
import woowacourse.shopping.data.shoppingCart.remote.dto.ShoppingCartItemsResponseDto
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import woowacourse.shopping.domain.shoppingCart.ShoppingCarts

fun ShoppingCartItemResponseDto.toDomain(): ShoppingCartProduct =
    ShoppingCartProduct(
        id = id,
        product = product.toDomain(),
        quantity = quantity,
    )

fun ShoppingCartItemsResponseDto.toDomain(): ShoppingCarts =
    ShoppingCarts(
        last = last,
        shoppingCartItems = shoppingCartItems.toDomain(),
    )

fun List<ShoppingCartItemResponseDto>.toDomain(): List<ShoppingCartProduct> = map(ShoppingCartItemResponseDto::toDomain)
