package woowacourse.shopping.presentation.view.order

import woowacourse.shopping.presentation.model.CartModel

interface OrderContract {
    interface View {
        val presenter: Presenter

        fun setAvailablePointView(point: Int)
        fun setSavingPoint(point: Int)
        fun setCartProductsView(products: List<CartModel>)
        fun handleErrorView()
    }

    interface Presenter {
        fun initReservedPoint()
        fun initSavingPoint()
        fun initCartProducts()
    }
}
