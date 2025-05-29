package woowacourse.shopping.view.cart

import woowacourse.shopping.domain.cart.CartItem

sealed interface CartItemType {
    val viewType: ItemType

    data class ProductItem(
        val cartItem: CartItem,
        var checked: Boolean = false,
    ) : CartItemType {
        val cartItemId: Long = cartItem.id
        override val viewType: ItemType = ItemType.PRODUCT
        val imageUrl = cartItem.imageUrl
        var quantity = cartItem.quantity
        val price get() = cartItem.productPrice * quantity

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ProductItem

            return cartItemId == other.cartItemId
        }

        override fun hashCode(): Int = cartItemId.hashCode()
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
