package woowacourse.shopping.presentation.order

import woowacourse.shopping.OrderCartInfoList
import woowacourse.shopping.Payment
import woowacourse.shopping.Point
import woowacourse.shopping.Price
import woowacourse.shopping.data.remote.order.response.OrderCartDataModel
import woowacourse.shopping.data.remote.order.response.OrderRequestDataModel
import woowacourse.shopping.data.remote.user.UserRepository
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.presentation.mapper.toDomain
import woowacourse.shopping.presentation.model.OrderCartModel

class OrderPresenter(
    private val view: OrderContract.View,
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository
) : OrderContract.Presenter {
    private lateinit var orderCarts: OrderCartInfoList
    private var totalPrice: Int = 0
    private var payment = Payment(Price(totalPrice), Point(0))

    override fun initOrderCarts(orderCarts: List<OrderCartModel>) {
        this.orderCarts = OrderCartInfoList(orderCarts.map { it.toDomain() })
        totalPrice = this.orderCarts.getTotalPrice()
        view.showOrderCarts(orderCarts)
    }

    override fun loadPoint() {
        userRepository.getPoint(
            onSuccess = {
                view.showPoint(it.value)
                payment = Payment(Price(totalPrice), Point(it.value))
            }, onFailure = {
            view.showPoint(0)
            payment = Payment(Price(totalPrice), Point(0))
        }
        )
    }

    override fun loadTotalPrice() {
        view.updateTotalPrice(totalPrice)
    }

    override fun checkPointOver(usingPoint: String) {
        val point = Point(usingPoint.toIntOrNull() ?: 0)
        if (payment.discountable(point))
            view.updateTotalPrice(payment.discount(point).value)
        else {
            view.showPointOver()
        }
    }

    override fun order(spendPoint: String) {
        orderRepository.order(
            OrderRequestDataModel(
                spendPoint.toInt(),
                orderCarts.value.map {
                    OrderCartDataModel(
                        it.product.id,
                        it.count
                    )
                }
            ),
            onSuccess = {
                view.showOrderSuccess()
            },
            onFailure = {
                view.showOrderFail()
            }
        )
    }
}
