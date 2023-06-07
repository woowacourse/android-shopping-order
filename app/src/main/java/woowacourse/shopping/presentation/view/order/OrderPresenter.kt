package woowacourse.shopping.presentation.view.order

import woowacourse.shopping.presentation.mapper.toModel
import woowacourse.shopping.presentation.mapper.toUIModel
import woowacourse.shopping.presentation.model.CardModel
import woowacourse.shopping.presentation.model.CartProductModel
import woowacourse.shopping.presentation.model.CartProductsModel
import woowacourse.shopping.presentation.model.OrderModel
import woowacourse.shopping.presentation.model.PointModel
import woowacouse.shopping.data.repository.card.CardRepository
import woowacouse.shopping.data.repository.cart.CartRepository
import woowacouse.shopping.data.repository.order.OrderRepository

class OrderPresenter(
    private val view: OrderContract.View,
    private val cardRepository: CardRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
) : OrderContract.Presenter {
    private lateinit var orderProducts: List<CartProductModel>
    private lateinit var point: PointModel
    private lateinit var cards: List<CardModel>
    private var orderPrice: Int = 0
    private var usePoint = PointModel(0)

    override fun setUsePoint(usePoint: Int) {
        this.usePoint = this.usePoint.toModel().updatePoint(usePoint).toUIModel()
        view.showUsePointView(this.usePoint)
    }

    override fun loadOrderProducts(cartIds: ArrayList<Long>) {
        cartRepository.loadCartsByCartIds(cartIds, ::onFailure) { carts ->
            orderProducts = carts.map { it.toUIModel() }

            orderPrice = orderProducts.sumOf { it.product.price * it.count }

            loadCards()
            loadUserPoint()
            loadSavePredictionPoint(orderPrice)

            view.showOrderPriceView(CartProductsModel(orderProducts).toModel().totalPrice)
            view.showProductItemsView(orderProducts)
            view.setLayoutVisibility()
        }
    }

    private fun loadCards() {
        cards = cardRepository.loadCards().map { it.toUIModel() }

        view.showCardItemsView(cards)
    }

    private fun loadUserPoint() {
        orderRepository.loadPoint(::onFailure) {
            point = it.toUIModel()
            view.showUserPointView(it.toUIModel())
            view.setPointTextChangeListener(orderPrice, point)
        }
    }

    private fun loadSavePredictionPoint(orderPrice: Int) {
        orderRepository.loadPredictionSavePoint(orderPrice, ::onFailure) {
            view.showSavePredictionPointView(it.toUIModel())
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
            cartRepository.deleteCarts(order.toModel().cartIds)
            view.showOrderDetailView(orderId)
        }
    }

    private fun onFailure(throwable: Throwable) {
        throwable.message?.let { view.handleErrorView(it) }
    }
}
