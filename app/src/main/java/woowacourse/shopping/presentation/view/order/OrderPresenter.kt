package woowacourse.shopping.presentation.view.order

import woowacourse.shopping.data.respository.point.PointRepository
import woowacourse.shopping.presentation.model.CartModel

class OrderPresenter(
    private val view: OrderContract.View,
    private val cartItems: List<CartModel>,
    private val pointRepository: PointRepository,
) : OrderContract.Presenter {

    override fun initPoint() {
        pointRepository.requestPoint(::onFailure) { pointEntity ->
            view.setAvailablePointView(pointEntity.point)
        }
    }

    fun onFailure() {
        view.handleErrorView()
    }
}
