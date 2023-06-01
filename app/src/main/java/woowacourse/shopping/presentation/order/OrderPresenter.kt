package woowacourse.shopping.presentation.order

import woowacourse.shopping.OrderCartInfoList
import woowacourse.shopping.data.order.OrderRepository
import woowacourse.shopping.data.user.UserRepository
import woowacourse.shopping.presentation.mapper.toDomain
import woowacourse.shopping.presentation.model.OrderCartInfoModel

class OrderPresenter(
    private val view: OrderContract.View,
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository
) : OrderContract.Presenter {
    private lateinit var orderCarts: OrderCartInfoList
    private var totalPrice: Int = orderCarts.getTotalPrice()

    override fun initOrderCarts(orderCarts: List<OrderCartInfoModel>) {
        this.orderCarts = OrderCartInfoList(orderCarts.map { it.toDomain() })
        view.showOrderCarts(orderCarts)
    }

    override fun loadPoint() {
        userRepository.getPoint({
            view.showPoint(it.value)
        }, {
            view.showPoint(0)
        })
    }

    override fun loadTotalPrice() {
        view.updateTotalPrice(totalPrice)
    }

    override fun checkPointOver(usingPoint: String) {
        view.updateTotalPrice(totalPrice - usingPoint.toInt())
    }

    override fun order() {
        orderRepository.order(
            onSuccess = {
                view.showOrderSuccess()
            },
            onFailure = {
                view.showOrderFail()
            }
        )
    }
}
