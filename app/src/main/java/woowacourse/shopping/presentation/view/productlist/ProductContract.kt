package woowacourse.shopping.presentation.view.productlist

import woowacourse.shopping.presentation.model.CartProductModel
import woowacourse.shopping.presentation.model.RecentProductModel

interface ProductContract {
    interface View {
        fun setProductItemsView(cartProducts: List<CartProductModel>)
        fun setRecentProductItemsView(recentProducts: List<RecentProductModel>)
        fun setVisibleToolbarCartCountView()
        fun setGoneToolbarCartCountView()
        fun setLayoutVisibility()
        fun updateToolbarCartCountView(count: Int)
        fun moveToCartView()
        fun handleErrorView(message: String)
    }

    interface Presenter {
        fun initRecentProductItems()
        fun initProductItems()
        fun setCartProductItems(products: List<CartProductModel>)
        fun loadRecentProductItems()
        fun loadCartItems()
        fun updateProductItems(startIndex: Int)
        fun saveRecentProduct(productId: Long)
        fun actionOptionItem()
        fun getLastRecentProductItem(lastRecentIndex: Int): RecentProductModel
        fun getRecentProductsLastScroll(): Int
        fun updateRecentProductsLastScroll(lastScroll: Int)
        fun updateCount(productId: Long, count: Int)
    }
}