package woowacourse.shopping.ui.order

import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.utils.LogUtil

class OrderPresenter(
    private val cartIds: List<Int>,
    private val orderRepository: OrderRepository
) : OrderContract.Presenter {
    override fun getOrderList() {
        orderRepository.getOrderList(cartIds) { result ->
            result.onSuccess { orderList -> println(orderList) }
                .onFailure { throwable -> LogUtil.logError(throwable) }
        }
    }
}
