package woowacourse.shopping.ui.order

import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.repository.OrderRepository
import woowacourse.shopping.uimodel.OrderInfoUIModel

class OrderPresenter(
    private val view: OrderContract.View,
    private val orderRepository: OrderRepository,
) : OrderContract.Presenter {
    private lateinit var orderInfo: OrderInfoUIModel

    override fun getOrderInfo(ids: List<Int>) {
        orderRepository.getOrderInfo(ids) {
            orderInfo = it?.toUIModel() ?: throw IllegalArgumentException("주문 정보를 가져오는데 실패했습니다.")
            view.initOrderPageInfo(orderInfo)
            view.updatePurchasePrice(0, orderInfo.totalPrice)
        }
    }

    override fun checkPointAvailable(pointPrice: Int?) {
        when {
            pointPrice == null -> {
                view.setButtonEnable(true)
            }

            pointPrice < 0 -> {
                view.setButtonEnable(false)
                view.showPointErrorMessage(OrderActivity.ERROR_POINT_INVALID_NUMBER)
            }

            pointPrice > orderInfo.availablePoints -> {
                view.setButtonEnable(false)
                view.showPointErrorMessage(OrderActivity.ERROR_POINT_UNAVAILABLE)
            }

            else -> {
                view.setButtonEnable(true)
            }
        }
    }

    override fun setTotalPrice(discountPrice: Int?) {
        when (discountPrice) {
            null -> {
                view.updatePurchasePrice(0, orderInfo.totalPrice)
            }

            else -> {
                view.updatePurchasePrice(discountPrice, orderInfo.totalPrice - discountPrice)
            }
        }
    }

    override fun order(point: Int) {
        val orderItemIds: List<Int> = orderInfo.cartItems.map { it.id }
        orderRepository.postOrder(orderItemIds, point) {
            view.showOrderSuccessMessage()
            view.navigateToShopping()
        }
    }
}
