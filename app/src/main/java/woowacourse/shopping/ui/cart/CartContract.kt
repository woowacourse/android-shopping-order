package woowacourse.shopping.ui.cart

import woowacourse.shopping.ui.cart.uistate.CartItemUIState
import woowacourse.shopping.ui.order.uistate.PaymentUIState

interface CartContract {

    interface Presenter {
        fun loadCartItemsOfNextPage()

        fun loadCartItemsOfPreviousPage()

        fun loadCartItemsOfLastPage()

        fun deleteCartItem(cartItemId: Long)

        fun updateSelectionCartItem(cartItemId: Long, isSelected: Boolean)

        fun updateSelectionTotalCartItems(isSelected: Boolean)

        fun plusCount(cartItemId: Long)

        fun minusCount(cartItemId: Long)

        fun checkPayment()

        fun placeOrder()
    }

    interface View {
        fun setStateThatCanRequestPreviousPage(canRequest: Boolean)

        fun setStateThatCanRequestNextPage(canRequest: Boolean)

        fun setStateThatCanRequestPage(canRequest: Boolean)

        fun setPage(page: Int)

        fun setCartItems(cartItems: List<CartItemUIState>, initScroll: Boolean)

        fun setStateOfAllSelection(isAllSelected: Boolean)

        fun setOrderPrice(price: Int)

        fun setOrderCount(count: Int)

        fun showPaymentWindow(payment: PaymentUIState)

        fun showOrderDetail(orderId: Long)
    }
}
