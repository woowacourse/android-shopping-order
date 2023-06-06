package woowacourse.shopping.presentation.cart

import woowacourse.shopping.presentation.model.CartProductModel
import woowacourse.shopping.presentation.model.OrderCartModel

interface CartContract {
    interface Presenter {

        fun checkPlusPageAble()
        fun checkMinusPageAble()
        fun addProductInOrder(cartProductModel: CartProductModel)
        fun deleteProductInOrder(cartProductModel: CartProductModel)
        fun updateProductCount(cartProductModel: CartProductModel, count: Int)
        fun deleteProductItem(cartProductModel: CartProductModel)
        fun plusPage()
        fun minusPage()
        fun refreshCurrentPage()
        fun changeCurrentPageProductsOrder(isOrdered: Boolean)
        fun checkCurrentPageProductsOrderState()
        fun updateOrderPrice()
        fun updateOrderCount()
        fun updateProductPrice(cartProductModel: CartProductModel)
        fun orderSelectedCart()
    }

    interface View {
        fun setCartItems(productModels: List<CartProductModel>)
        fun setUpPlusPageState(isEnable: Boolean)
        fun setUpMinusPageState(isEnable: Boolean)
        fun setOrderPrice(totalPrice: Int)
        fun setOrderCount(count: Int)
        fun setAllOrderState(isAllOrder: Boolean)
        fun setProductPrice(price: Int)
        fun setPage(page: String)
        fun setLoadingViewVisible(isVisible: Boolean)
        fun showOrderView(orderCarts: List<OrderCartModel>)
    }
}
