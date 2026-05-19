package woowacourse.shopping.ui.cart

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import woowacourse.shopping.model.CartItem
import woowacourse.shopping.ui.model.ProductUiModel

fun ImmutableList<ProductUiModel>.mapper(cartItems: List<CartItem>): ImmutableList<ProductUiModel> {
    val quantityByProductId = cartItems.associate { it.product.id to it.quantity }

    return map { product ->
        product.copy(quantity = quantityByProductId[product.id])
    }.toImmutableList()
}
