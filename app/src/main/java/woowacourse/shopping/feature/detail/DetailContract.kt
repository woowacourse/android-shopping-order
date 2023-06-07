package woowacourse.shopping.feature.detail

import androidx.lifecycle.LiveData
import woowacourse.shopping.model.ProductUiModel
import woowacourse.shopping.model.RecentProductUiModel

interface DetailContract {
    interface View {
        fun showFailedLoadProductInfo()
        fun showCartScreen()
        fun hideRecentScreen()
        fun setRecentScreen(title: String, money: String)
        fun showRecentProductDetailScreen(recentProductUiModel: RecentProductUiModel)
        fun exitDetailScreen()
        fun showSelectCartProductCountScreen(product: ProductUiModel, cartId: Long?)
        fun showRetryMessage()
        fun showNetworkError()
    }

    interface Presenter {
        val product: LiveData<ProductUiModel>
        fun initPresenter()
        fun updateProductCount(count: Int)
        fun navigateRecentProductDetail()
        fun handleAddCartClick()
        fun setProductCountInfo(count: Int)
        fun exit()
    }
}
