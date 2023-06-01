package woowacourse.shopping.presentation.view.order

import woowacourse.shopping.presentation.model.CardModel
import woowacourse.shopping.presentation.model.CartModel

interface OrderContract {
    interface View {
        fun setLayoutVisibility()
        fun setProductItemsView(products: List<CartModel>)
        fun setCardItemsView(cards: List<CardModel>)
        fun handleErrorView()
    }

    interface Presenter {
        fun loadOrderProducts(cartIds: ArrayList<Long>)
    }
}
