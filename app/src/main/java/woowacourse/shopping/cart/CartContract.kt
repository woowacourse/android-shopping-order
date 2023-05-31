package woowacourse.shopping.cart

import woowacourse.shopping.common.model.CartProductModel

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
    }

    interface View {
        fun updateCart(cartProducts: List<CartProductModel>, currentPage: Int, isLastPage: Boolean)

        fun updateNavigationVisibility(visibility: Boolean)

        fun updateCartTotalPrice(price: Int)

        fun updateCartTotalQuantity(amount: Int)

        fun setResultForChange()

        fun updateCartProduct(cartProduct: CartProductModel)

        fun updateAllChecked(isAllChecked: Boolean)

        fun notifyLoadFailed()
    }
}
