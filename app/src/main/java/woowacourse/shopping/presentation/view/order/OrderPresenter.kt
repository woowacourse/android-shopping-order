package woowacourse.shopping.presentation.view.order

import android.text.Editable
import com.example.domain.cart.CartProducts
import woowacourse.shopping.R
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.mapper.toUiModel
import woowacourse.shopping.data.model.OrderPostEntity
import woowacourse.shopping.data.respository.order.OrderRepository
import woowacourse.shopping.data.respository.point.PointRepository
import woowacourse.shopping.presentation.model.CartModel

class OrderPresenter(
    private val view: OrderContract.View,
    cartItems: List<CartModel>,
    private val pointRepository: PointRepository,
    private val orderRepository: OrderRepository,
) : OrderContract.Presenter {
    private val cartItemsDomain = CartProducts(cartItems.map { it.toDomain() })

    override fun initReservedPoint() {
        pointRepository.requestReservedPoint(::onFailure) { pointEntity ->
            view.setAvailablePointView(pointEntity.point)
        }
    }

    override fun initSavingPoint() {
        pointRepository.requestSavingPoint(
            cartItemsDomain.totalCheckedPrice,
            ::onFailure,
        ) { savingPointEntity ->
            view.setSavingPoint(savingPointEntity.savingPoint)
        }
    }

    override fun initCartProducts() {
        view.setCartProductsView(cartItemsDomain.all.map { it.toUiModel() })
    }

    override fun initOrderDetail() {
        view.setTotalPriceView(cartItemsDomain.totalCheckedPrice)
    }

    override fun setPoint(s: Editable?, availablePoint: Int) {
        if (s == null || s.isEmpty()) {
            return
        }

        val usedPoint = s.toString().toIntOrNull() ?: return

        if (usedPoint > availablePoint) {
            val message = view.getMessage(R.string.toast_message_point_error)
            view.handleErrorView(message)
            view.clearUsedPointView()
            return
        }

        val totalPrice = cartItemsDomain.totalCheckedPrice
        view.setOrderPriceView(usedPoint, totalPrice)
    }

    override fun order(usedPoint: Int) {
        val cartIds = cartItemsDomain.getCheckedCartProductsId()
        val orderPostEntity = OrderPostEntity(cartIds, DUMMY_CARD_NUMBER, DUMMY_CARD_CVC, usedPoint)
        orderRepository.requestPostOrder(orderPostEntity, ::onFailure) { orderId ->
            view.moveToOrderDetail(orderId)
        }
    }

    fun onFailure() {
        val message = view.getMessage(R.string.toast_message_system_error)
        view.handleErrorView(message)
    }

    companion object {
        private const val DUMMY_CARD_NUMBER = "1234-1234-1234-1234"
        private const val DUMMY_CARD_CVC = 327
    }
}
