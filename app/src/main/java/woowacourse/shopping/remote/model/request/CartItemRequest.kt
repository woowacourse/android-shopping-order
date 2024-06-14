package woowacourse.shopping.remote.model.request

import woowacourse.shopping.data.model.ProductIdsCountData

data class CartItemRequest(
    val productId: Long,
    val quantity: Int,
)

fun ProductIdsCountData.toRequest(): CartItemRequest =
    CartItemRequest(
        productId = productId,
        quantity = quantity,
    )
