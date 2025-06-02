package woowacourse.shopping.view.shoppingCart

import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import woowacourse.shopping.view.common.QuantityObservable

sealed interface ShoppingCartItem {
    val viewType: ItemType

    data class ShoppingCartProductItem(
        val shoppingCartProduct: ShoppingCartProduct,
        val isChecked: Boolean = false,
    ) : ShoppingCartItem,
        QuantityObservable {
        override val viewType: ItemType = ItemType.PRODUCT
    }

    enum class ItemType {
        PRODUCT,
        ;

        companion object {
            fun from(viewType: Int): ItemType = entries[viewType]
        }
    }
}
