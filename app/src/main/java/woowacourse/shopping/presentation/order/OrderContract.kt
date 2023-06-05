package woowacourse.shopping.presentation.order

import woowacourse.shopping.presentation.model.CartProductModel

interface OrderContract {
    interface Presenter {
        fun loadOrderCarts(orderCartIds: List<Long>)
        fun loadCash()
        fun chargeCash(cash: Int)
        fun orderCartProducts()
    }

    interface View {
        fun showOrderCartProducts(cartProductModels: List<CartProductModel>)
        fun showCash(cash: Int)
        fun showTotalPrice(totalPrice: Int)
        fun showOrderDetail(orderId: Long)
        fun handleRequestError()
    }
}
