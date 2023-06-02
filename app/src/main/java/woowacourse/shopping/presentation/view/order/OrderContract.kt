package woowacourse.shopping.presentation.view.order

import woowacourse.shopping.presentation.model.CardModel
import woowacourse.shopping.presentation.model.CartModel
import woowacourse.shopping.presentation.model.PointModel

interface OrderContract {
    interface View {
        fun setLayoutVisibility()
        fun setProductItemsView(products: List<CartModel>)
        fun setCardItemsView(cards: List<CardModel>)
        fun setUserPointView(userPoint: PointModel)
        fun setUsePointView(usePoint: PointModel)
        fun setPointTextChangeListener(userPoint: PointModel)
        fun setOrderPriceView(orderPrice: Int)
        fun setSavePredictionPointView(savePredictionPoint: PointModel)
        fun showOrderDetailView(orderId: Long)
        fun handleErrorView()
    }

    interface Presenter {
        fun setUsePoint(usePoint: Int)
        fun loadOrderProducts(cartIds: ArrayList<Long>)
        fun postOrder()
    }
}
