package woowacourse.shopping.remote.model.response

import woowacourse.shopping.data.model.CartItemData

data class CartItemResponse(
    val id: Long,
    val quantity: Int,
    val product: ProductResponse,
)

fun CartItemResponse.toData(): CartItemData =
    CartItemData(
        id = id,
        quantity = quantity,
        product = product.toData(),
    )

fun List<CartItemResponse>.toData(): List<CartItemData> = map(CartItemResponse::toData)
