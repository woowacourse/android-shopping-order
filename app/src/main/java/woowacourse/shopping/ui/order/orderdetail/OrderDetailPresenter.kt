package woowacourse.shopping.ui.order.orderdetail

import android.os.Handler
import android.os.Looper
import woowacourse.shopping.repository.OrderRepository
import woowacourse.shopping.ui.order.uistate.OrderUIState.Companion.toUIState
import woowacourse.shopping.ui.order.uistate.PaymentUIState.Companion.toUIState
import woowacourse.shopping.utils.ErrorHandler.handle

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderRepository: OrderRepository
) : OrderDetailContract.Presenter {
    private val mainLooperHandler = Handler(Looper.getMainLooper())

    override fun loadOrder(orderId: Long) {
        orderRepository.findById(orderId).thenAccept {
            val order = it.getOrThrow()
            val payment = orderRepository.findDiscountPolicy(order.price).get().getOrThrow()
            mainLooperHandler.post {
                view.showOrder(order.toUIState(), payment.toUIState())
            }
        }.exceptionally {
            it.handle(view)
            null
        }
    }
}
