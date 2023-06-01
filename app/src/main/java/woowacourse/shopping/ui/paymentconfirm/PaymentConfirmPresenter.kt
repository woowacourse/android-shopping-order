package woowacourse.shopping.ui.paymentconfirm

import woowacourse.shopping.domain.Basket
import woowacourse.shopping.domain.BasketProduct
import woowacourse.shopping.domain.UserPointInfo
import woowacourse.shopping.domain.repository.PointRepository
import woowacourse.shopping.ui.mapper.toUi

class PaymentConfirmPresenter(
    override val view: PaymentConfirmContract.View,
    private val pointRepository: PointRepository,
    currentOrderBasketProducts: List<BasketProduct>
) : PaymentConfirmContract.Presenter {
    private lateinit var userPointInfo: UserPointInfo
    private val currentOrderBasket = Basket(currentOrderBasketProducts)

    init {
        fetchUserPointInfo()
    }

    override fun fetchUserPointInfo() {
        pointRepository.getUserPointInfo {
            userPointInfo = it
            view.updateUserPointInfo(userPointInfo.toUi())
        }
    }
}
