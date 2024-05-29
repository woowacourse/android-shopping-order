package woowacourse.shopping.data.cart.model

import woowacourse.shopping.domain.entity.Product

data class CartPageData(
    val cartItems: List<CartItemData>,
    val pageNumber: Int,
    val totalPageSize: Int,
    val pageSize: Int,
    val totalProductSize: Int,
)

data class CartItemData(
    val cartId: Long,
    val count: Int,
    val product: Product
)