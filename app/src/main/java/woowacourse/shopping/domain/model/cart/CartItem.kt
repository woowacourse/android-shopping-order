package woowacourse.shopping.domain.model.cart

import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.domain.model.selector.ItemSelector

data class CartItem(
    val id: Long = DEFAULT_CART_ITEM_ID,
    val product: Product,
    val cartItemSelector: ItemSelector = ItemSelector(),
) {
    companion object {
        const val DEFAULT_CART_ITEM_ID = -1L
    }
}
