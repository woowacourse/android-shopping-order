package woowacourse.shopping.presentation.productlist

import woowacourse.shopping.presentation.model.CartProductModel
import woowacourse.shopping.presentation.model.ProductModel

interface ProductListContract {
    interface Presenter {
        fun refreshProductItems()
        fun loadRecentProductItems()
        fun updateCartCount()
        fun loadMoreProductItems()
        fun updateCartItemQuantity(cartProductModel: CartProductModel, count: Int)
        fun showMyCart()
        fun addCartItem(cartProductModel: CartProductModel)
    }

    interface View {
        fun loadProductItems(cartProductModels: List<CartProductModel>)
        fun loadRecentProductItems(productModels: List<ProductModel>)
        fun showCartCount(count: Int)
        fun navigateToCart(cartProductModels: List<CartProductModel>)
        fun setLoadingViewVisible(isVisible: Boolean)
        fun showErrorForServerView()
    }
}
