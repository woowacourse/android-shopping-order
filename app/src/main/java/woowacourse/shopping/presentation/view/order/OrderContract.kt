package woowacourse.shopping.presentation.view.order

import woowacourse.shopping.presentation.model.CardModel
import woowacourse.shopping.presentation.model.CartProductModel
import woowacourse.shopping.presentation.model.PointModel

interface OrderContract {
    interface View {
        fun setLayoutVisibility()
        fun setPointTextChangeListener(orderPrice: Int, userPoint: PointModel)
        fun showProductItemsView(products: List<CartProductModel>)
        fun showCardItemsView(cards: List<CardModel>)
        fun showUserPointView(userPoint: PointModel)
        fun showUsePointView(usePoint: PointModel)
        fun showOrderPriceView(orderPrice: Int)
        fun showSavePredictionPointView(savePredictionPoint: PointModel)
        fun showOrderDetailView(orderId: Long)
        fun handleErrorView(messageId: Int)
    }

    interface Presenter {
        fun setUsePoint(usePoint: Int)
        fun loadOrderProducts(cartIds: ArrayList<Long>)
        fun postOrder()
    }
}
