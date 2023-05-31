package woowacourse.shopping.feature.cart

import androidx.lifecycle.LiveData
import woowacourse.shopping.model.CartBottomNavigationUiModel
import woowacourse.shopping.model.CartProductUiModel
import woowacourse.shopping.model.PageBottomNavigationUiModel

interface CartContract {
    interface View {
        fun exitCartScreen()
        fun hideLoadingView()
        fun showLoadingView()
        fun navigateToOrder(cartId: List<Long>)
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
        fun processOrderClick()
        fun loadPreviousPage()
        fun loadNextPage()
        fun exit()
    }
}
