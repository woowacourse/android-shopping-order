package woowacourse.shopping.feature.payment

import woowacourse.shopping.model.CartProductUiModel
import woowacourse.shopping.model.PointUiModel

interface PaymentContract {
    interface View {
        fun showCartProducts(cartProducts: List<CartProductUiModel>)
        fun showPoint(point: PointUiModel)
        fun showPaymentDoneScreen()
        fun setPoint(usedPoint: Int)
    }

    interface Presenter {
        val view: View
        fun loadCartProducts(cartProductIds: List<Int>)
        fun loadPoint()
        fun placeOrder(usedPoint: Int)
        fun useAllPoint()
    }
}
