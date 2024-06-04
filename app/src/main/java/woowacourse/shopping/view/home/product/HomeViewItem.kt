package woowacourse.shopping.view.home.product

import woowacourse.shopping.domain.model.ProductItemDomain

sealed class HomeViewItem(open val viewType: Int) {
    data class ProductViewItem(
        val product: ProductItemDomain,
        private var _quantity: Int = DEFAULT_SHOPPING_QUANTITY,
        override val viewType: Int = PRODUCT_VIEW_TYPE,
        val cartItemId: Int? = null,
    ) : HomeViewItem(viewType) {
        val quantity: Int
            get() = _quantity

        fun increase(): ProductViewItem = copy(_quantity = quantity + 1)

        fun decrease(): ProductViewItem = copy(_quantity = quantity - 1)

        fun isQuantityControlVisible(): Boolean = quantity >= 1

        companion object {
            private const val DEFAULT_SHOPPING_QUANTITY = 0
        }
    }

    data class LoadMoreViewItem(
        override val viewType: Int = LOAD_MORE_BUTTON_VIEW_TYPE,
    ) : HomeViewItem(viewType)

    data class ProductPlaceHolderViewItem(
        override val viewType: Int = PRODUCT_PLACEHOLDER_VIEW_TYPE,
    ) : HomeViewItem(viewType)

    companion object {
        const val PRODUCT_PLACEHOLDER_VIEW_TYPE = 0
        const val PRODUCT_VIEW_TYPE = 1
        const val LOAD_MORE_BUTTON_VIEW_TYPE = 2
    }
}
