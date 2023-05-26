package woowacourse.shopping.ui.productlist

import woowacourse.shopping.ui.productlist.uistate.ProductUIState
import woowacourse.shopping.ui.productlist.uistate.RecentlyViewedProductUIState

interface ProductListContract {
    interface Presenter {
        fun loadRecentlyViewedProducts()
        fun loadProductsNextPage()
        fun addProductToCart(productId: Long)
        fun refreshProducts()
        fun plusCount(cartItemId: Long)
        fun minusCount(cartItemId: Long)
        fun loadCartItemCount()
        fun openCart()
    }

    interface View {
        fun setRecentlyViewedProducts(recentlyViewedProducts: List<RecentlyViewedProductUIState>)
        fun addProducts(products: List<ProductUIState>)
        fun changeProduct(product: ProductUIState)
        fun setProducts(products: List<ProductUIState>)
        fun setCanLoadMore(canLoadMore: Boolean)
        fun setCartItemCount(count: Int)
        fun showCart()
    }
}
