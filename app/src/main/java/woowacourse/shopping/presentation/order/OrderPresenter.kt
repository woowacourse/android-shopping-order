package woowacourse.shopping.presentation.order

import android.util.Log
import woowacourse.shopping.OrderCartInfoList
import woowacourse.shopping.data.order.OrderRepository
import woowacourse.shopping.data.order.response.OrderCartDataModel
import woowacourse.shopping.data.order.response.OrderRequestDataModel
import woowacourse.shopping.data.user.UserRepository
import woowacourse.shopping.presentation.mapper.toDomain
import woowacourse.shopping.presentation.model.OrderCartInfoModel

class OrderPresenter(
    private val view: OrderContract.View,
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository
) : OrderContract.Presenter {
    private lateinit var orderCarts: OrderCartInfoList
    private var totalPrice: Int = 0

    override fun initOrderCarts(orderCarts: List<OrderCartInfoModel>) {
        this.orderCarts = OrderCartInfoList(orderCarts.map { it.toDomain() })
        totalPrice = this.orderCarts.getTotalPrice()
        view.showOrderCarts(orderCarts)
    }

    override fun loadPoint() {
        userRepository.getPoint({
            Log.d("test1", it.value.toString())
            view.showPoint(it.value)
        }, {
            Log.d("test2", "0")
            view.showPoint(0)
        })
    }

    override fun loadTotalPrice() {
        view.updateTotalPrice(totalPrice)
    }

    override fun checkPointOver(usingPoint: String) {
        view.updateTotalPrice(totalPrice - usingPoint.toInt())
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
