package woowacourse.shopping.ui.cart

import androidx.lifecycle.LiveData
import woowacourse.shopping.model.CartProductUIModel
import woowacourse.shopping.model.PageUIModel

interface CartContract {

    interface View {
        fun setPage(page: List<CartProductUIModel>, pageUIModel: PageUIModel)
        fun navigateToItemDetail(productId: Int)
        fun navigateToOrderCheckout(cartIds: List<Int>)
    }

    interface Presenter {
        val totalPrice: LiveData<Int>
        val checkedCount: LiveData<Int>
        val allCheck: LiveData<Boolean>
        fun fetchCartProducts()
        fun moveToPageNext()
        fun moveToPagePrev()
        fun getPageIndex(): Int
        fun updateItemsCheck(checked: Boolean)
        fun updateItemCount(productId: Int, count: Int)
        fun updateItemCheck(productId: Int, checked: Boolean)
        fun processToItemDetail(productId: Int)
        fun processToOrderCheckout()
    }
}
