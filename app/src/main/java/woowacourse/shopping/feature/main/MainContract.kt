package woowacourse.shopping.feature.main

import androidx.lifecycle.LiveData
import woowacourse.shopping.model.CartProductUiModel
import woowacourse.shopping.model.ProductUiModel
import woowacourse.shopping.model.RecentProductUiModel

interface MainContract {
    interface View {
        sealed class MainScreenEvent {
            object ShowCartScreen : MainScreenEvent()
            object ShowOrderListScreen : MainScreenEvent()
            class ShowProductDetailScreen(
                val product: ProductUiModel,
                val recentProduct: RecentProductUiModel?,
            ) : MainScreenEvent()

            object ShowFailedLoadProduct : MainScreenEvent()
            object HideLoadMore : MainScreenEvent()
            object ShowLoading : MainScreenEvent()
            object HideLoading : MainScreenEvent()
            object ShowNetworkError : MainScreenEvent()
            object ShowRetryMessage : MainScreenEvent()
        }
    }

    interface Presenter {
        val products: LiveData<List<CartProductUiModel>>
        val recentProducts: LiveData<List<RecentProductUiModel>>
        val badgeCount: LiveData<Int>
        val mainScreenEvent: LiveData<View.MainScreenEvent>
        fun initLoadProducts()
        fun loadMoreProducts()
        fun loadRecentProducts()
        fun showCartCount()
        fun showProductDetail(productId: Long)
        fun changeProductCartCount(productId: Long, count: Int)
        fun moveToCart()
        fun moveToOrderList()
    }
}
