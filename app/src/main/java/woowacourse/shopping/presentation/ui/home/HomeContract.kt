package woowacourse.shopping.presentation.ui.home

import woowacourse.shopping.domain.model.Operator
import woowacourse.shopping.domain.model.RecentlyViewedProduct
import woowacourse.shopping.presentation.model.HomeData

interface HomeContract {
    interface View {
        val presenter: Presenter
        fun setHomeData(homeData: List<HomeData>)
        fun initRecentlyViewed()
        fun updateRecentlyViewedProducts(products: List<RecentlyViewedProduct>)
        fun appendProductItems(startPosition: Int, size: Int)
        fun appendShowMoreItem(position: Int)
        fun removeShowMoreItem(position: Int)
        fun showError(message: String)
        fun updateProductQuantity(position: Int)
        fun updateTotalQuantity(size: Int)
        fun notifyLoadingFinished()
    }

    interface Presenter {
        fun setHome()
        fun fetchMoreProducts()
        fun fetchRecentlyViewed()
        fun updateProductQuantity(position: Int, operator: Operator)
        fun fetchTotalQuantity()
    }
}
