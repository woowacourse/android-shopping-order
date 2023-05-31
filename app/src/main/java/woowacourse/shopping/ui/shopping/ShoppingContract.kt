package woowacourse.shopping.ui.shopping

import woowacourse.shopping.domain.user.User
import woowacourse.shopping.ui.shopping.uistate.ProductUIState
import woowacourse.shopping.ui.shopping.uistate.RecentlyViewedProductUIState

interface ShoppingContract {
    interface Presenter {
        fun loadRecentlyViewedProducts()
        fun loadProductsNextPage()
        fun addProductToCart(productId: Long)
        fun refreshProducts()
        fun plusCount(cartItemId: Long)
        fun minusCount(cartItemId: Long)
        fun loadCartItemCount()
        fun openCart()
        fun openOrderList()
        fun loadUsers()
        fun selectUser(user: User)
    }

    interface View {
        fun setRecentlyViewedProducts(recentlyViewedProducts: List<RecentlyViewedProductUIState>)
        fun addProducts(products: List<ProductUIState>)
        fun changeProduct(product: ProductUIState)
        fun setProducts(products: List<ProductUIState>)
        fun setCanLoadMore(canLoadMore: Boolean)
        fun setCartItemCount(count: Int)
        fun showCart()
        fun showOrderList()
        fun showUserList(users: List<User>)
    }
}
