package woowacourse.shopping.data.order.source

import woowacourse.shopping.data.product.source.NetworkProduct

data class NetworkOrder(
    val id: Long,
    val totalPrice: Int,
    val cartItems: List<NetworkOrderItem>
)

data class NetworkOrderItem(
    val id: Long?,
    val quantity: Int,
    val product: NetworkProduct
)
