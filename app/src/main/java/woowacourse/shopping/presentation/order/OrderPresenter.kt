package woowacourse.shopping.presentation.order

import woowacourse.shopping.presentation.mapper.toDomain
import woowacourse.shopping.presentation.model.OrderProductsModel
import woowacourse.shopping.repository.OrderRepository
import woowacourse.shopping.repository.UserRepository

class OrderPresenter constructor(
    private val view: OrderContract.View,
    private val orderProductsModel: OrderProductsModel,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
) : OrderContract.Presenter {
    private val orderProducts = orderProductsModel.toDomain()
    private var userTotalPoint = 2000
    private var usagePoint = 0

    override fun loadOrderItems() {
        view.setOrderItems(orderProductsModel.list)
    }

    override fun showUserTotalPoint() {
        userRepository.getUserPoint {
            userTotalPoint = it?.point?.value ?: 2000
            view.setUserTotalPoint(userTotalPoint)
        }
    }

    override fun checkPointAble(usePointText: String) {
        usagePoint = usePointText.toIntOrNull() ?: 0
        when {
            usagePoint < MINIMUM_POINT -> view.setUsagePoint("")
            usagePoint > userTotalPoint -> view.setUsagePoint(userTotalPoint.toString())
        }
    }

    override fun showOrderPrice() {
        val price = orderProducts.totalPrice
        view.setOrderPrice(price)
    }

    override fun showPaymentPrice() {
        val price = orderProducts.totalPrice - usagePoint
        view.setPaymentPrice(price)
    }

    override fun addOrder() {
        orderRepository.addOrder(usagePoint, orderProducts) {
            view.showAddOrderComplete(it)
        }
    }

    companion object {
        private const val MINIMUM_POINT = 0
    }
}
