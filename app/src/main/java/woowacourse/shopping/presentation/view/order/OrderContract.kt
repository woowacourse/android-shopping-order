package woowacourse.shopping.presentation.view.order

import android.text.Editable
import woowacourse.shopping.presentation.model.CartModel

interface OrderContract {
    interface View {
        val presenter: Presenter

        fun getMessage(resourceId: Int): String
        fun setAvailablePointView(point: Int)
        fun setSavingPoint(point: Int)
        fun setCartProductsView(products: List<CartModel>)
        fun setTotalPriceView(totalPrice: Int)
        fun setOrderPriceView(point: Int, totalPrice: Int)
        fun clearUsedPointView()
        fun handleErrorView(message: String)
    }

    interface Presenter {
        fun initReservedPoint()
        fun initSavingPoint()
        fun initCartProducts()
        fun initOrderDetail()
        fun setPoint(s: Editable?, availablePoint: Int)
        fun order(usedPoint: Int)
    }
}
