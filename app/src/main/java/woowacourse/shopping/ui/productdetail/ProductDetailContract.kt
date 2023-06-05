package woowacourse.shopping.ui.productdetail

import woowacourse.shopping.model.ProductModel
import woowacourse.shopping.model.RecentProductModel

interface ProductDetailContract {
    interface View {
        fun showProductDetail(product: ProductModel)
        fun showLastViewedProductDetail(lastViewedProduct: ProductModel?)
        fun showProductCounter(product: ProductModel)
        fun navigateToProductDetail(recentProduct: RecentProductModel)
        fun navigateToHome(product: ProductModel, count: Int)
    }

    interface Presenter {
        fun navigateToHome(count: Int)
        fun inquiryProductCounter()
        fun inquiryLastViewedProduct()
    }
}
