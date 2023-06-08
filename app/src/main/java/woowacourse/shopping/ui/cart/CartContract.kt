package woowacourse.shopping.ui.cart

import androidx.lifecycle.LiveData
import woowacourse.shopping.model.CartProductPageUIModel

interface CartContract {

    interface View {
        fun navigateToItemDetail(productId: Int)
        fun navigateToOrderCheckout(cartIds: List<Int>)
    }

    interface Presenter {
        val page: LiveData<CartProductPageUIModel>
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
