package woowacourse.shopping.presentation.view.order

import woowacourse.shopping.presentation.mapper.toUIModel
import woowacourse.shopping.presentation.model.CardModel
import woowacourse.shopping.presentation.model.CartModel
import woowacouse.shopping.data.repository.card.CardRepository
import woowacouse.shopping.data.repository.cart.CartRepository

class OrderPresenter(
    private val view: OrderContract.View,
    private val cardRepository: CardRepository,
    private val cartRepository: CartRepository,
) : OrderContract.Presenter {
    private lateinit var orderProducts: List<CartModel>
    private lateinit var cards: List<CardModel>

    override fun loadOrderProducts(cartIds: ArrayList<Long>) {
        cartRepository.loadCartsByCartIds(cartIds, ::onFailure) { carts ->
            orderProducts = carts.map { it.toUIModel() }

            loadCards()

            view.setProductItemsView(orderProducts)
            view.setLayoutVisibility()
        }
    }

    private fun loadCards() {
        cards = cardRepository.loadCards().map { it.toUIModel() }

        view.setCardItemsView(cards)
    }

    private fun onFailure() {
        view.handleErrorView()
    }
}
