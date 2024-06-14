package woowacourse.shopping.data.model

import woowacourse.shopping.ui.model.CartItem

data class CartItemData(
    val id: Long,
    val quantity: Int,
    val product: ProductData,
)

fun CartItemData.toDomain(): CartItem {
    return CartItem(
        id = id,
        product = product.toDomain(),
        quantity = quantity,
        checked = false,
    )
}

fun List<CartItemData>.toDomain(): List<CartItem> = map(CartItemData::toDomain)
