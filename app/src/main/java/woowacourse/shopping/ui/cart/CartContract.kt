package woowacourse.shopping.ui.cart

import woowacourse.shopping.uimodel.CartProductUIModel
import woowacourse.shopping.uimodel.PageUIModel
import woowacourse.shopping.uimodel.ProductUIModel
import woowacourse.shopping.utils.NonNullLiveData

interface CartContract {

    interface View {
        fun setPage(page: List<CartProductUIModel>, pageUIModel: PageUIModel)
        fun navigateToItemDetail(product: ProductUIModel)
        fun navigateToOrder(checkedIds: List<Int>)
        fun showEmptyOrderMessage()
    }

    interface Presenter {
        val totalPrice: NonNullLiveData<Int>
        val checkedCount: NonNullLiveData<Int>
        val allCheck: NonNullLiveData<Boolean>
        fun setUpView()
        fun setUpProductsCheck(checked: Boolean)
        fun moveToPageNext()
        fun moveToPagePrev()
        fun updateItemCount(productId: Int, count: Int)
        fun updateItemCheck(productId: Int, checked: Boolean)
        fun removeItem(productId: Int)
        fun getPageIndex(): Int
        fun navigateToItemDetail(productId: Int)
        fun navigateToOrder()
    }
}
