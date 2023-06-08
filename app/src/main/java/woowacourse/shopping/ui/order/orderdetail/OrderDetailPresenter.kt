package woowacourse.shopping.ui.order.orderdetail

import android.os.Handler
import android.os.Looper
import woowacourse.shopping.repository.OrderRepository
import woowacourse.shopping.repository.UserRepository
import woowacourse.shopping.ui.order.uistate.OrderUIState.Companion.toUIState
import woowacourse.shopping.ui.order.uistate.PaymentUIState.Companion.toUIState
import woowacourse.shopping.utils.ErrorHandler.handle

class OrderDetailPresenter(
    private val view: OrderDetailContract.View,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository
) : OrderDetailContract.Presenter {
    private val mainLooperHandler = Handler(Looper.getMainLooper())

    override fun loadOrder(orderId: Long) {
        userRepository.findCurrent().thenAccept {
            val currentUser = it.getOrThrow()
            val order = orderRepository.findById(orderId, currentUser).get().getOrThrow()
            val payment =
                orderRepository.findDiscountPolicy(order.price, currentUser.rank.toString()).get()
                    .getOrThrow()
            mainLooperHandler.post {
                view.showOrder(order.toUIState(), payment.toUIState())
            }
        }.exceptionally {
            it.handle(view)
            null
        }
    }
}
