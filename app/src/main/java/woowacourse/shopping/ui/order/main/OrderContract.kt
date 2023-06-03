package woowacourse.shopping.ui.order.main

import woowacourse.shopping.domain.model.Point
import woowacourse.shopping.model.UiCartProduct
import woowacourse.shopping.model.UiPrice

interface OrderContract {
    interface View {
        fun showOrderProductList(cartProducts: List<UiCartProduct>)
        fun showAvailablePoint(point: Point)
        fun watchUsedPoints()
        fun showTotalPayment(totalPayment: UiPrice)
        fun showFinalPayment(finalPayment: UiPrice)
        fun onOrderClickListener()
        fun navigateToHome(orderedProductCount: Int)
    }

    abstract class Presenter(protected val view: View) {
        abstract fun loadOrderProducts()
        abstract fun loadAvailablePoints()
        abstract fun loadPayment()
        abstract fun calculateFinalPayment(point: Int)
        abstract fun order()
        abstract fun navigateToHome(itemId: Int)
    }
}
