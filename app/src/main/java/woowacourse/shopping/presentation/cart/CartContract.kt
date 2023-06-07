package woowacourse.shopping.presentation.cart

import woowacourse.shopping.presentation.model.CartProductInfoModel
import woowacourse.shopping.presentation.model.OrderProductsModel

interface CartContract {
    interface Presenter {

        fun checkPlusPageAble()
        fun checkMinusPageAble()
        fun plusPage()
        fun minusPage()
        fun addProductInOrder(cartProductModel: CartProductInfoModel)
        fun deleteProductInOrder(cartProductModel: CartProductInfoModel)
        fun changeCurrentPageProductsOrder(isOrdered: Boolean)
        fun checkCurrentPageProductsIsOrdered()
        fun updateOrderPrice()
        fun updateOrderCount()
        fun updateProductCount(cartProductModel: CartProductInfoModel, count: Int)
        fun deleteProductItem(cartProductModel: CartProductInfoModel)
        fun refreshCurrentPageItems()
        fun loadCartItems()
        fun order()
    }

    interface View {
        fun setCartItems(productModels: List<CartProductInfoModel>)
        fun setUpPlusPageState(isEnable: Boolean)
        fun setUpMinusPageState(isEnable: Boolean)
        fun setOrderPrice(totalPrice: Int)
        fun setOrderCount(count: Int)
        fun setAllIsOrderCheck(isAllOrder: Boolean)
        fun setPage(page: String)
        fun setLoadingViewVisible(isVisible: Boolean)
        fun showOrderView(orderProductsModel: OrderProductsModel)
    }
}
