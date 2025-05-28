package woowacourse.shopping.view.product

import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.view.common.QuantityObservable
import woowacourse.shopping.view.product.ProductsItem.ItemType.entries

sealed interface ProductsItem {
    val viewType: ItemType

    data class RecentWatchingItem(
        val products: List<ProductItem>,
    ) : ProductsItem {
        override val viewType: ItemType
            get() = ItemType.RECENT_WATCHING
    }

    data class ProductItem(
        val shoppingCartId: Long? = null,
        val product: Product,
        val selectedQuantity: Int = 0,
    ) : ProductsItem,
        QuantityObservable {
        override val viewType: ItemType = ItemType.PRODUCT
    }

    data object LoadItem : ProductsItem {
        override val viewType: ItemType = ItemType.MORE
    }

    enum class ItemType {
        PRODUCT,
        MORE,
        RECENT_WATCHING,
        ;

        companion object {
            fun from(viewType: Int): ItemType = entries[viewType]
        }
    }
}
