package woowacourse.shopping.presentation.view.productdetail

import woowacourse.shopping.presentation.model.ProductModel
import woowacourse.shopping.presentation.model.RecentProductModel

interface ProductDetailContract {
    interface View {
        fun setVisibleOfLastRecentProductInfoView(recentProduct: RecentProductModel)
        fun setGoneOfLastRecentProductInfoView()
        fun setProductInfoView(productModel: ProductModel)
        fun showCountView(productModel: ProductModel)
        fun handleErrorView()
        fun addCartSuccessView()
        fun exitProductDetailView()
    }
    interface Presenter {
        fun setProduct(productId: Long)
        fun loadLastRecentProductInfo(recentProduct: RecentProductModel?)
        fun addCart(productId: Long, count: Int)
        fun showCount()
    }
}
