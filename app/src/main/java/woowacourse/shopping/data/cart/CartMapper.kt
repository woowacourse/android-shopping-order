package woowacourse.shopping.data.cart

import woowacourse.shopping.data.cart.model.CartItemData
import woowacourse.shopping.data.cart.model.CartPageData
import woowacourse.shopping.data.shopping.product.toProduct
import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.entity.CartProduct
import woowacourse.shopping.remote.dto.response.CartItemResponse
import woowacourse.shopping.remote.dto.response.CartItemsResponse

fun CartItemResponse.toData(): CartItemData {
    return CartItemData(
        cartId = id,
        count = count,
        product = product.toProduct(),
    )
}

fun CartItemsResponse.toData(): CartPageData {
    return CartPageData(
        cartItems = cartItems.map { it.toData() },
        pageNumber = pageConfig.pageNumber,
        totalPageSize = totalPageSize,
        pageSize = pageConfig.pageSize,
        totalProductSize = totalProductSize,
    )
}

fun CartPageData.toDomain(): Cart {
    return cartItems.map { it.toDomain() }.let(::Cart)
}

fun CartItemData.toDomain(): CartProduct {
    return CartProduct(
        id = cartId,
        product = product,
        count = count,
    )
}
