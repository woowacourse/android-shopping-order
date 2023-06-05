package woowacourse.shopping.ui.productdetail

import woowacourse.shopping.ui.ErrorView
import woowacourse.shopping.ui.productdetail.uistate.LastViewedProductUIState
import woowacourse.shopping.ui.productdetail.uistate.ProductDetailUIState

interface ProductDetailContract {
    interface Presenter {
        fun loadProduct(productId: Long)
        fun addProductToCart(productId: Long, count: Int)
        fun showCartCounter(productId: Long)
        fun loadLastViewedProduct(limit: Int)
    }

    interface View : ErrorView {
        fun setProduct(product: ProductDetailUIState)
        fun setLastViewedProduct(product: LastViewedProductUIState?)
        fun openCartCounter(product: ProductDetailUIState)
        fun showCartView()
        override fun showError(message: String)
    }
}
