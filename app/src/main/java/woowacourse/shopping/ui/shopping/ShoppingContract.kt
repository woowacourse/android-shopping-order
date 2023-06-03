package woowacourse.shopping.ui.shopping

import woowacourse.shopping.model.ProductUIModel
import woowacourse.shopping.model.RecentProductUIModel

interface ShoppingContract {
    interface View {
        fun addMoreProducts(products: List<ProductUIModel>)
        fun setRecentProducts(recentProductsData: List<RecentProductUIModel>)
        fun setCartProducts(cartCounts: Map<Int, Int>)
        fun setToolbar(totalCount: Int)
        fun navigateToProductDetail(productId: Int)
        fun navigateToOrders()
    }

    interface Presenter {
        fun setUpNextProducts()
        fun setUpRecentProducts()
        fun setUpCartCounts()
        fun setUpTotalCount()
        fun updateItemCount(productId: Int, count: Int)
        fun navigateToItemDetail(productId: Int)
        fun navigateToOrders()
    }
}
