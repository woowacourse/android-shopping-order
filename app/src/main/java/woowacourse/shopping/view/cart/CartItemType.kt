package woowacourse.shopping.view.cart

import woowacourse.shopping.domain.product.CartItem

sealed interface CartItemType {
    val viewType: ItemType

    data class ProductItem(
        val cartItem: CartItem,
    ) : CartItemType {
        override val viewType: ItemType = ItemType.PRODUCT
        val imageUrl = cartItem.imageUrl
        var quantity = cartItem.quantity
    }

    data class PaginationItem(
        val page: Int,
        val previousEnabled: Boolean,
        val nextEnabled: Boolean,
    ) : CartItemType {
        override val viewType: ItemType = ItemType.PAGINATION

        val enabled: Boolean = previousEnabled || nextEnabled
    }

    enum class ItemType {
        PRODUCT,
        PAGINATION,
        ;

        companion object {
            fun from(viewType: Int): ItemType = entries[viewType]
        }
    }
}
