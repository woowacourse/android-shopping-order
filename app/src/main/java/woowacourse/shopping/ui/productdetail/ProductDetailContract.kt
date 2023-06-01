package woowacourse.shopping.ui.productdetail

import woowacourse.shopping.model.ProductModel
import woowacourse.shopping.model.UiRecentProduct

interface ProductDetailContract {
    interface View {
        fun showProductDetail(product: ProductModel)
        fun showLastViewedProductDetail(lastViewedProduct: ProductModel?)
        fun showProductCounter(product: ProductModel)
        fun navigateToProductDetail(recentProduct: UiRecentProduct)
        fun navigateToHome(product: ProductModel, count: Int)
    }

    abstract class Presenter(protected val view: View) {
        abstract fun navigateToHome(count: Int)
        abstract fun inquiryProductCounter()
        abstract fun inquiryLastViewedProduct()
    }
}
