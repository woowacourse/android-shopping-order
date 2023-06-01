package woowacourse.shopping.ui.cart

import woowacourse.shopping.model.CartProductModel
import woowacourse.shopping.model.OrderModel
import woowacourse.shopping.model.PageModel

interface CartContract {
    interface View {
        fun updateCart(cartProducts: List<CartProductModel>)
        fun updateNavigatorEnabled(previousEnabled: Boolean, nextEnabled: Boolean)
        fun updatePageNumber(page: PageModel)
        fun updateTotalPrice(totalPrice: Int)
        fun navigateToOrder(order: OrderModel)
        fun navigateToHome()
        fun showErrorMessage(message: String)
    }

    abstract class Presenter(protected val view: View) {
        abstract fun fetchCart(page: Int)
        abstract fun order()
        abstract fun deleteProduct(cartProduct: CartProductModel)
        abstract fun updateProductCount(cartProduct: CartProductModel, count: Int)
        abstract fun toggleAllCheckState()
        abstract fun updateProductSelectState(cartProduct: CartProductModel, isSelect: Boolean)
        abstract fun navigateToHome()
    }
}
