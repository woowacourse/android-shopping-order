package woowacourse.shopping.ui.productdetail

import woowacourse.shopping.model.UiProduct
import woowacourse.shopping.model.UiRecentProduct

interface ProductDetailContract {
    interface View {
        fun showProductDetail(product: UiProduct)
        fun showLastViewedProductDetail(lastViewedProduct: UiProduct?)
        fun showProductCounter(product: UiProduct)
        fun navigateToProductDetail(recentProduct: UiRecentProduct)
        fun navigateToHome(product: UiProduct, count: Int)
    }

    abstract class Presenter(protected val view: View) {
        abstract fun navigateToHome(count: Int)
        abstract fun inquiryProductCounter()
        abstract fun inquiryLastViewedProduct()
    }
}
