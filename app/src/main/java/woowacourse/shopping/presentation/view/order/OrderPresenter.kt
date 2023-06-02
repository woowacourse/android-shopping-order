package woowacourse.shopping.presentation.view.order

import woowacourse.shopping.presentation.mapper.toModel
import woowacourse.shopping.presentation.mapper.toUIModel
import woowacourse.shopping.presentation.model.CardModel
import woowacourse.shopping.presentation.model.CartModel
import woowacourse.shopping.presentation.model.CartProductsModel
import woowacourse.shopping.presentation.model.OrderModel
import woowacourse.shopping.presentation.model.PointModel
import woowacouse.shopping.data.repository.card.CardRepository
import woowacouse.shopping.data.repository.cart.CartRepository
import woowacouse.shopping.data.repository.order.OrderRepository
import woowacouse.shopping.data.repository.point.PointRepository

class OrderPresenter(
    private val view: OrderContract.View,
    private val cardRepository: CardRepository,
    private val cartRepository: CartRepository,
    private val pointRepository: PointRepository,
    private val orderRepository: OrderRepository,
) : OrderContract.Presenter {
    private lateinit var orderProducts: List<CartModel>
    private lateinit var point: PointModel
    private lateinit var cards: List<CardModel>
    private var usePoint = PointModel(0)

    override fun setUsePoint(usePoint: Int) {
        this.usePoint = this.usePoint.toModel().updatePoint(usePoint).toUIModel()
        view.setUsePointView(this.usePoint)
    }

    override fun loadOrderProducts(cartIds: ArrayList<Long>) {
        cartRepository.loadCartsByCartIds(cartIds, ::onFailure) { carts ->
            orderProducts = carts.map { it.toUIModel() }

            val orderPrice = orderProducts.sumOf { it.product.price }

            loadCards()
            loadUserPoint()
            loadSavePredictionPoint(orderPrice)

            view.setOrderPriceView(CartProductsModel(orderProducts).toModel().totalPrice)
            view.setProductItemsView(orderProducts)
            view.setLayoutVisibility()
        }
    }

    private fun loadCards() {
        cards = cardRepository.loadCards().map { it.toUIModel() }

        view.setCardItemsView(cards)
    }

    private fun loadUserPoint() {
        pointRepository.loadPoint(::onFailure) {
            point = it.toUIModel()
            view.setUserPointView(it.toUIModel())
            view.setPointTextChangeListener(point)
        }
    }

    private fun loadSavePredictionPoint(orderPrice: Int) {
        pointRepository.loadPredictionSavePoint(orderPrice, ::onFailure) {
            view.setSavePredictionPointView(it.toUIModel())
        }
    }

    override fun postOrder() {
        val order = OrderModel(
            999999999L,
            CartProductsModel(orderProducts),
            usePoint,
            cards[0],
        )
        orderRepository.addOrder(order.toModel(), ::onFailure) { orderId ->
            cartRepository.deleteLocalCarts(order.toModel().cartIds)
            view.showOrderDetailView(orderId)
        }
    }

    private fun onFailure() {
        view.handleErrorView()
    }
}
