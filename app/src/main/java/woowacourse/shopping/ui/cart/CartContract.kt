package woowacourse.shopping.ui.cart

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.model.Page
import woowacourse.shopping.model.UiCartProduct

interface CartContract {
    interface View {
        fun updateCart(cartProducts: List<UiCartProduct>)
        fun updateNavigatorEnabled(previousEnabled: Boolean, nextEnabled: Boolean)
        fun updatePageNumber(page: Page)
        fun updateTotalPrice(totalPrice: Int)
        fun showOrderComplete(cartProducts: List<CartProduct>, productCount: Int)
        fun showOrderFailed()
        fun showLoadFailed(error: String)
        fun navigateToHome()
        fun navigateToOrder(cartProducts: List<CartProduct>)
    }

    abstract class Presenter(protected val view: View) {
        abstract fun fetchCart(page: Int)
        abstract fun changeProductCount(cartProduct: UiCartProduct, count: Int)
        abstract fun changeProductSelectState(cartProduct: UiCartProduct, isSelect: Boolean)
        abstract fun toggleAllCheckState()
        abstract fun removeProduct(cartProduct: UiCartProduct)
        abstract fun order()
        abstract fun navigateToHome()
        abstract fun navigateToOrder()
    }
}
