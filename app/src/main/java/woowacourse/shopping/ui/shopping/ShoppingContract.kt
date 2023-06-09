package woowacourse.shopping.ui.shopping

import woowacourse.shopping.model.ProductUIModel
import woowacourse.shopping.model.RecentProductUIModel

interface ShoppingContract {
    interface View {
        fun setMoreProducts(products: List<ProductUIModel>)
        fun setRecentProducts(recentProductsData: List<RecentProductUIModel>)
        fun setCartProducts(cartCounts: Map<Int, Int>)
        fun setToolbar(totalCount: Int)
        fun navigateToProductDetail(productId: Int)
        fun navigateToOrderHistories()
    }

    interface Presenter {
        fun fetchNextProducts()
        fun fetchRecentProducts()
        fun fetchCartCounts()
        fun fetchTotalCount()
        fun updateItemCount(productId: Int, count: Int)
        fun processToItemDetail(productId: Int)
        fun processToOrderHistories()
    }
}
