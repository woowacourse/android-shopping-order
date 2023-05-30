package woowacourse.shopping.presentation.productlist

import woowacourse.shopping.presentation.model.CartProductInfoModel
import woowacourse.shopping.presentation.model.ProductModel

interface ProductListContract {
    interface Presenter {
        fun refreshProductItems()
        fun loadRecentProductItems()
        fun updateCartCount()
        fun loadMoreProductItems()
        fun updateCartItemQuantity(cartProductModel: CartProductInfoModel, count: Int)
        fun showMyCart()
        fun addCartItem(cartProductModel: CartProductInfoModel)
    }

    interface View {
        fun loadProductItems(cartProductModels: List<CartProductInfoModel>)
        fun loadRecentProductItems(productModels: List<ProductModel>)
        fun showCartCount(count: Int)
        fun navigateToCart(cartProductInfoModels: List<CartProductInfoModel>)
        fun setLoadingViewVisible(isVisible: Boolean)
    }
}
