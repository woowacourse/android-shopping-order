package woowacourse.shopping.data.cart.source

import woowacourse.shopping.data.product.source.NetworkProduct

data class NetworkCartItem(
    val id: Long,
    val quantity: Int,
    val product: NetworkProduct
)
