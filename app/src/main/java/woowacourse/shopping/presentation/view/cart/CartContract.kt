package woowacourse.shopping.presentation.view.cart

import woowacourse.shopping.presentation.model.CartProductModel

interface CartContract {
    interface View {
        fun setEnableLeftButton(isEnabled: Boolean)
        fun setEnableRightButton(isEnabled: Boolean)
        fun setEnableOrderButton(isEnabled: Boolean)
        fun setAllCartChecked(isChecked: Boolean)
        fun setLayoutVisibility()
        fun showCartItemsView(carts: List<CartProductModel>)
        fun showChangedCartItemsView(carts: List<CartProductModel>)
        fun showPageCountView(page: Int)
        fun showTotalPriceView(totalPrice: Int)
        fun showOrderView(cartIds: ArrayList<Long>)
        fun handleErrorView(message: String)
    }

    interface Presenter {
        fun initCartItems()
        fun setPageNation(cartProducts: List<CartProductModel>, currentPage: Int)
        fun loadCartItems()
        fun deleteCartItem(cartId: Long)
        fun setPreviousPage()
        fun setNextPage()
        fun calculateTotalPrice()
        fun updateProductCount(cartId: Long, count: Int)
        fun updateProductChecked(cartId: Long, isChecked: Boolean)
        fun updateCurrentPageAllProductChecked(isChecked: Boolean)
        fun showOrder()
    }
}
