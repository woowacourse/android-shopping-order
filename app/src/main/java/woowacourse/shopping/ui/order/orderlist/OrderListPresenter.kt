package woowacourse.shopping.ui.order.orderlist

import android.os.Handler
import android.os.Looper
import woowacourse.shopping.repository.OrderRepository
import woowacourse.shopping.repository.UserRepository
import woowacourse.shopping.ui.order.uistate.OrderUIState.Companion.toUIState
import woowacourse.shopping.utils.ErrorHandler.handle

class OrderListPresenter(
    private val view: OrderListContract.View,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository
) : OrderListContract.Presenter {
    private val mainLooperHandler = Handler(Looper.getMainLooper())

    override fun loadOrders() {
        userRepository.findCurrent().thenCompose {
            val currentUser = it.getOrThrow()
            orderRepository.findAll(currentUser)
        }.thenAccept { ordersResult ->
            val orders = ordersResult.getOrThrow()
            mainLooperHandler.post {
                view.showOrders(orders.map { it.toUIState() })
            }
        }.exceptionally {
            it.handle(view)
            null
        }
    }

    override fun openOrderDetail(orderId: Long) {
        view.showOrderDetail(orderId)
    }
}
