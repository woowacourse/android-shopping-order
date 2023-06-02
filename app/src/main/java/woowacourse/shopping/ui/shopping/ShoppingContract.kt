package woowacourse.shopping.ui.shopping

import woowacourse.shopping.model.CartProductModel
import woowacourse.shopping.model.ProductCountModel
import woowacourse.shopping.model.ProductModel
import woowacourse.shopping.model.RecentProductModel

interface ShoppingContract {
    interface View {
        fun updateProducts(products: List<CartProductModel>)
        fun updateRecentProducts(recentProducts: List<RecentProductModel>)
        fun navigateToProductDetail(product: ProductModel)
        fun navigateToCart()
        fun showLoadMoreButton()
        fun hideLoadMoreButton()
        fun updateCartBadge(count: ProductCountModel)
        fun showErrorMessage(message: String)
        fun navigateToOrderList()
    }

    interface Presenter {
        fun fetchAll()
        fun fetchRecentProducts()
        fun loadMoreProducts()
        fun addCartProduct(product: ProductModel, addCount: Int = 1)
        fun updateCartCount(cartProduct: CartProductModel, changedCount: Int)
        fun increaseCartCount(product: ProductModel, addCount: Int)
        fun navigateToCart()
        fun inquiryProductDetail(cartProduct: CartProductModel)
        fun inquiryRecentProductDetail(recentProduct: RecentProductModel)
        fun inquiryOrders()
    }
}
