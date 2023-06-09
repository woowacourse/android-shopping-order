package woowacourse.shopping.ui.cart

import woowacourse.shopping.ui.model.CartProductModel

interface CartContract {
    interface Presenter {
        fun removeCartProduct(cartProductModel: CartProductModel)

        fun goToPreviousPage()

        fun goToNextPage()

        fun reverseCartProductChecked(cartProductModel: CartProductModel)

        fun updateAllChecked()

        fun decreaseCartProductQuantity(cartProductModel: CartProductModel)

        fun increaseCartProductQuantity(cartProductModel: CartProductModel)

        fun changeAllChecked(isChecked: Boolean)

        fun order()
    }

    interface View {
        fun updateCart(cartProducts: List<CartProductModel>, currentPage: Int, isLastPage: Boolean)

        fun updateNavigationVisibility(visibility: Boolean)

        fun updateCartTotalPrice(price: Int)

        fun updateCartTotalQuantity(amount: Int)

        fun setResultForChange()

        fun updateCartProduct(cartProduct: CartProductModel)

        fun updateAllChecked(isAllChecked: Boolean)

        fun notifyFailure(message: String)

        fun showOrder(ids: List<Int>)
    }
}
