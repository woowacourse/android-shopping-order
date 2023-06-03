package woowacourse.shopping.presentation.view.cart

import woowacourse.shopping.presentation.model.CartModel

interface CartContract {
    interface View {
        fun setCartItemsView(carts: List<CartModel>)
        fun setChangedCartItemsView(carts: List<CartModel>)
        fun setEnableLeftButton(isEnabled: Boolean)
        fun setEnableRightButton(isEnabled: Boolean)
        fun setEnableOrderButton(isEnabled: Boolean)
        fun setAllCartChecked(isChecked: Boolean)
        fun setPageCountView(page: Int)
        fun setTotalPriceView(totalPrice: Int)
        fun setLayoutVisibility()
        fun showOrderView(cartIds: ArrayList<Long>)
        fun handleErrorView()
    }

    interface Presenter {
        fun initCartItems()
        fun setPageNation(cartProducts: List<CartModel>, currentPage: Int)
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
