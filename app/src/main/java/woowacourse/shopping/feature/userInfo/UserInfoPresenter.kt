package woowacourse.shopping.feature.userInfo

import com.example.domain.model.Order
import com.example.domain.repository.OrderRepository
import com.example.domain.repository.PointRepository
import woowacourse.shopping.mapper.toPresentation

class UserInfoPresenter(
    private val view: UserInfoContract.View,
    private val orderRepository: OrderRepository,
    private val pointRepository: PointRepository
) : UserInfoContract.Presenter {

    private var orders = mutableListOf<Order>()
    private var page = 1

    override fun loadOrders() {
        orderRepository.getOrders(
            page = page,
            onSuccess = { newOrders ->
                orders.addAll(newOrders)
                ++page
                view.showOrders(orders.map { it.toPresentation() })
            },
            onFailure = { view.showFailureMessage(it.message) }
        )
    }

    override fun loadPoint() {
        pointRepository.getPoint(
            onSuccess = {
                view.showPoint(point = it.toPresentation())
            },
            onFailure = { view.showFailureMessage(it.message) }
        )
    }
}
