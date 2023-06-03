package woowacourse.shopping.ui.paymentconfirm

import woowacourse.shopping.domain.Basket
import woowacourse.shopping.domain.BasketProduct
import woowacourse.shopping.domain.UserPointInfo
import woowacourse.shopping.domain.exception.AddOrderException.LackOfPointException
import woowacourse.shopping.domain.exception.AddOrderException.ShortageStockException
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.repository.PointRepository
import woowacourse.shopping.ui.mapper.toUi
import woowacourse.shopping.ui.model.preorderinfo.PreOrderInfoFactory

class PaymentConfirmPresenter(
    override val view: PaymentConfirmContract.View,
    private val pointRepository: PointRepository,
    private val orderRepository: OrderRepository,
    currentOrderBasketProducts: List<BasketProduct>
) : PaymentConfirmContract.Presenter {
    private lateinit var userPointInfo: UserPointInfo
    private val currentOrderBasket = Basket(currentOrderBasketProducts)
    private var usingPoint = 0
    private val totalPrice = currentOrderBasket.getTotalPrice()
    private val actualPayment: Int
        get() = totalPrice - usingPoint

    init {
        fetchUserPointInfo()
        fetchPreOrderInfo()
        initPointData()
    }

    private fun initPointData() {
        view.updateUsingPoint(usingPoint)
        view.updateActualPayment(actualPayment)
    }

    override fun fetchUserPointInfo() {
        pointRepository.getUserPointInfo {
            userPointInfo = it
            view.updateUserPointInfo(userPointInfo.toUi())
        }
    }

    override fun fetchPreOrderInfo() {
        view.updatePreOrderInfo(PreOrderInfoFactory(currentOrderBasket).getPreOrderInfo())
    }

    override fun applyPoint(input: Int) {
        if (input <= userPointInfo.point.value) {
            usingPoint = input
            view.updatePointMessageCode(ApplyPointMessageCode.AVAILABLE_TO_APPLY)
            view.updateUsingPoint(usingPoint)
            view.updateActualPayment(actualPayment)
        } else {
            view.updatePointMessageCode(ApplyPointMessageCode.OVER_USE_POINT)
        }
    }

    override fun addOrder() {
        orderRepository.addOrder(
            basketProductsId = currentOrderBasket.products.map { it.id },
            usingPoint = usingPoint,
            orderTotalPrice = totalPrice
        ) { result ->
            result
                .onSuccess { view.showOrderSuccessNotification() }
                .onFailure {
                    when (it) {
                        is ShortageStockException -> {
                            view.showOrderShortageStockFailureNotification(it.detailMessage)
                        }
                        is LackOfPointException -> {
                            view.showOrderLackOfPointFailureNotification(it.detailMessage)
                        }
                    }
                }
        }
    }
}
