package woowacourse.shopping.ui.home.adapter.product

import woowacourse.shopping.domain.model.Product

sealed class HomeViewItem(open val viewType: Int) {
    data class ProductViewItem(
        val product: Product,
        private var _quantity: Int = DEFAULT_SHOPPING_QUANTITY,
        override val viewType: Int = PRODUCT_VIEW_TYPE,
    ) : HomeViewItem(viewType) {
        val quantity: Int
            get() = _quantity

        fun isQuantityControlVisible(): Boolean {
            return quantity >= 1
        }

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
