package woowacourse.shopping.view.shoppingCart

import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import woowacourse.shopping.view.common.QuantityObservable
import woowacourse.shopping.view.shoppingCart.ShoppingCartItem.ItemType.entries

sealed interface ShoppingCartItem {
    val viewType: ItemType

    data class ShoppingCartProductItem(
        val shoppingCartProduct: ShoppingCartProduct,
    ) : ShoppingCartItem,
        QuantityObservable {
        override val viewType: ItemType = ItemType.PRODUCT
    }

    data class PaginationItem(
        val page: Int,
        val nextEnabled: Boolean,
        val previousEnabled: Boolean,
    ) : ShoppingCartItem {
        override val viewType: ItemType = ItemType.PAGINATION
    }

    enum class ItemType {
        PRODUCT,
        PAGINATION, ;

        companion object {
            fun from(viewType: Int): ItemType = entries[viewType]
        }
    }
}
