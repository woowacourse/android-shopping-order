package woowacourse.shopping.ui.cart

import woowacourse.shopping.model.Order
import woowacourse.shopping.model.Page
import woowacourse.shopping.model.UiCartProduct

interface CartContract {
    interface View {
        fun updateCart(cartProducts: List<UiCartProduct>)
        fun updateNavigatorEnabled(previousEnabled: Boolean, nextEnabled: Boolean)
        fun updatePageNumber(page: Page)
        fun updateTotalPrice(totalPrice: Int)
        fun navigateToOrder(order: Order)
        fun navigateToHome()
        fun showErrorMessage(message: String)
    }

    abstract class Presenter(protected val view: View) {
        abstract fun fetchCart(page: Int)
        abstract fun order()
        abstract fun removeProduct(cartProduct: UiCartProduct)
        abstract fun changeProductCount(cartProduct: UiCartProduct, count: Int)
        abstract fun toggleAllCheckState()
        abstract fun changeProductSelectState(cartProduct: UiCartProduct, isSelect: Boolean)
        abstract fun navigateToHome()
    }
}
