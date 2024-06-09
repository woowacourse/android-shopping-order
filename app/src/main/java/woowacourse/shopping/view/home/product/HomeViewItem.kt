package woowacourse.shopping.view.home.product

import woowacourse.shopping.domain.model.OrderableProduct

sealed class HomeViewItem(open val viewType: Int) {
    data class ProductViewItem(
        val orderableProduct: OrderableProduct,
        override val viewType: Int = PRODUCT_VIEW_TYPE,
    ) : HomeViewItem(viewType) {
        fun isQuantityControlVisible(): Boolean = (orderableProduct.cartData?.quantity ?: 0) >= 1
    }

    data class ProductPlaceHolderViewItem(
        override val viewType: Int = PRODUCT_PLACEHOLDER_VIEW_TYPE,
    ) : HomeViewItem(viewType)

    companion object {
        const val PRODUCT_PLACEHOLDER_VIEW_TYPE = 0
        const val PRODUCT_VIEW_TYPE = 1
        const val LOAD_MORE_BUTTON_VIEW_TYPE = 2
    }
}
