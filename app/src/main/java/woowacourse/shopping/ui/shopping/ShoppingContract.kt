package woowacourse.shopping.ui.shopping

import woowacourse.shopping.model.CartProductModel
import woowacourse.shopping.model.ProductCount
import woowacourse.shopping.model.ProductModel
import woowacourse.shopping.model.UiRecentProduct

interface ShoppingContract {
    interface View {
        fun updateProducts(products: List<CartProductModel>)
        fun updateRecentProducts(recentProducts: List<UiRecentProduct>)
        fun navigateToProductDetail(product: ProductModel)
        fun navigateToCart()
        fun showLoadMoreButton()
        fun hideLoadMoreButton()
        fun updateCartBadge(count: ProductCount)
        fun showErrorMessage(message: String)
        fun navigateToOrderList()
    }

    abstract class Presenter(protected val view: View) {
        abstract fun fetchAll()
        abstract fun fetchRecentProducts()
        abstract fun loadMoreProducts()
        abstract fun addCartProduct(product: ProductModel, addCount: Int = 1)
        abstract fun updateCartCount(cartProduct: CartProductModel, changedCount: Int)
        abstract fun increaseCartCount(product: ProductModel, addCount: Int)
        abstract fun navigateToCart()
        abstract fun inquiryProductDetail(cartProduct: CartProductModel)
        abstract fun inquiryRecentProductDetail(recentProduct: UiRecentProduct)
        abstract fun inquiryOrders()
    }
}
