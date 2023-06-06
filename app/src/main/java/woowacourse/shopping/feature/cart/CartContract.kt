package woowacourse.shopping.feature.cart

import woowacourse.shopping.model.CartProductState
import woowacourse.shopping.model.CartState

interface CartContract {

    interface View {
        fun setCartProducts(cartProducts: List<CartProductState>)
        fun setCartPageNumber(number: Int)
        fun setCartPageNumberPlusEnable(isEnable: Boolean)
        fun setCartPageNumberMinusEnable(isEnable: Boolean)
        fun setCartProductCount(count: Int)
        fun setTotalCost(paymentAmount: Int)
        fun setAllPickChecked(checked: Boolean)
        fun showPageSelectorView()
        fun showCartProducts()
        fun hidePageSelectorView()
        fun updateItem(newItem: CartProductState)
        fun showOrderPage(cart: CartState)
    }

    interface Presenter {
        fun initContents()
        fun loadCart()
        fun plusPageNumber()
        fun minusPageNumber()
        fun plusQuantity(cartProductState: CartProductState)
        fun minusQuantity(cartProductState: CartProductState)
        fun updatePickedByCartId(cartId: Long, checked: Boolean)
        fun updatePaymentAmount()
        fun updatePickedCartProductCount()
        fun deleteCartProduct(cartProductState: CartProductState)
        fun changeAllPicked()
        fun pickAll()
        fun attachCartToOrder()
    }
}
