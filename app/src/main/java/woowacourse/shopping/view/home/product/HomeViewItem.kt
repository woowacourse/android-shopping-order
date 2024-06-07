package woowacourse.shopping.view.home.product

import woowacourse.shopping.domain.model.OrderableProduct
import woowacourse.shopping.domain.model.ProductItemDomain

sealed class HomeViewItem(open val viewType: Int) {
    data class ProductViewItem(
        val orderableProduct: OrderableProduct,
//        private var _quantity: Int = DEFAULT_SHOPPING_QUANTITY,
        override val viewType: Int = PRODUCT_VIEW_TYPE,
    ) : HomeViewItem(viewType) {
//        val quantity: Int
//            get() = _quantity
//
//        fun increase(): ProductViewItem = copy(_quantity = quantity + 1)
//
//        fun decrease(): ProductViewItem = copy(_quantity = quantity - 1)

        fun isQuantityControlVisible(): Boolean = (orderableProduct.cartData?.quantity ?: 0) >= 1

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
