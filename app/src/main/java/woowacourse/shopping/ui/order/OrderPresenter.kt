package woowacourse.shopping.ui.order

import woowacourse.shopping.model.OrderInfo
import woowacourse.shopping.repository.CartRepository

class OrderPresenter(
    private val view: OrderContract.View,
    private val cartRepository: CartRepository,
) : OrderContract.Presenter {
    private lateinit var orderInfo: OrderInfo

    override fun getOrderInfo(ids: List<Int>) {
        cartRepository.getOrderInfo(ids) {
            orderInfo = it ?: throw IllegalArgumentException("주문 정보를 가져오는데 실패했습니다.")
        }
    }
}
