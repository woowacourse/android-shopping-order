package woowacourse.shopping.presentation.view.productlist

import woowacourse.shopping.presentation.model.CartProductModel
import woowacourse.shopping.presentation.model.RecentProductModel

interface ProductContract {
    interface View {
        fun setVisibleToolbarCartCountView()
        fun setLayoutVisibility()
        fun showProductItemsView(cartProducts: List<CartProductModel>)
        fun showRecentProductItemsView(recentProducts: List<RecentProductModel>)
        fun hideGoneToolbarCartCountView()
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
