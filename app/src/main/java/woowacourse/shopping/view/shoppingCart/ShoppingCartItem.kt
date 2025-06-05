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

    data class OrderBarItem(
        val totalPrice: Int,
        val totalQuantity: Int,
        val isAllSelected: Boolean,
        val shoppingCartProductsToOrder: List<ShoppingCartProduct>,
        val isOrderEnabled: Boolean,
    ) : ShoppingCartItem {
        override val viewType: ItemType = ItemType.ORDER_BAR
    }

    enum class ItemType {
        PRODUCT,
        ORDER_BAR,
        ;

        companion object {
            fun from(viewType: Int): ItemType = entries[viewType]
        }
    }
}
