package woowacourse.shopping.ui.cart

import woowacourse.shopping.ui.cart.uistate.CartItemUIState
import woowacourse.shopping.ui.cart.uistate.OrderPriceInfoUIState

interface CartContract {

    interface Presenter {

        fun onLoadCartItemsOfFirstPage()

        fun onLoadCartItemsOfLastPage()

        fun onLoadCartItemsOfPreviousPage()

        fun onLoadCartItemsOfNextPage()

        fun onDeleteCartItem(cartItemId: Long)

        fun onChangeSelectionOfCartItem(cartItemId: Long, isSelected: Boolean)

        fun onChangeSelectionOfAllCartItems(isSelected: Boolean)

        fun onPlusCount(cartItemId: Long)

        fun onMinusCount(cartItemId: Long)

        fun onOrderSelectedCartItems()

        fun onLoadOrderPriceInfo()
    }

    interface View {
        fun setStateThatCanRequestPreviousPage(canRequest: Boolean)

        fun setStateThatCanRequestNextPage(canRequest: Boolean)

        fun setPageUIVisibility(isVisible: Boolean)

        fun setPage(page: Int)

        fun setCartItems(cartItems: List<CartItemUIState>, initScroll: Boolean)

        fun setStateOfAllSelection(isAllSelected: Boolean)

        fun setOrderPrice(price: Int)

        fun setOrderCount(count: Int)

        fun setCanOrder(canOrder: Boolean)

        fun showOrderResult(orderId: Long)

        fun setCanSeeOrderPriceInfo(canSeeOrderPriceInfo: Boolean)

        fun showOrderPriceInfo(orderPriceInfo: OrderPriceInfoUIState)

        fun showMessage(message: String)

        fun refresh()
    }
}
