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

    interface Presenter {
        fun fetchCart(page: Int)
        fun order()
        fun deleteProduct(cartProductModel: CartProductModel)
        fun updateProductCount(cartProductModel: CartProductModel, count: Int)
        fun toggleAllCheckState()
        fun updateProductSelectState(cartProductModel: CartProductModel, isSelect: Boolean)
        fun navigateToHome()
    }
}
