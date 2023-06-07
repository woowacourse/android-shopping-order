package woowacourse.shopping.feature.cart

import androidx.lifecycle.LiveData
import woowacourse.shopping.model.CartBottomNavigationUiModel
import woowacourse.shopping.model.CartProductUiModel
import woowacourse.shopping.model.PageBottomNavigationUiModel

interface CartContract {
    interface View {
        fun showFailedLoadCartInfo()
        fun reBindProductItem(cartId: Long)
        fun exitCartScreen()
        fun hideLoadingView()
        fun showLoadingView()
        fun showOrderConfirmScreen(cartIds: List<Long>)
        fun showOrderUnavailableMessage()
        fun showFailedChangeCartCount()
        fun showFailedOrderRequest()
        fun showNetworkError()
    }

    interface Presenter {
        val currentPageCartProducts: LiveData<List<CartProductUiModel>>
        val pageBottomNavigationUiModel: LiveData<PageBottomNavigationUiModel>
        val cartBottomNavigationUiModel: LiveData<CartBottomNavigationUiModel>
        fun loadInitCartProduct()
        fun handleDeleteCartProductClick(cartId: Long)
        fun handleCartProductCartCountChange(cartId: Long, count: Int)
        fun handlePurchaseSelectedCheckedChange(cartId: Long, checked: Boolean)
        fun handleCurrentPageAllCheckedChange(checked: Boolean)
        fun requestOrderConfirmScreen()
        fun processRemoveOrderCheckedItems()
        fun loadPreviousPage()
        fun loadNextPage()
        fun exit()
    }
}
