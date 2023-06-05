package woowacourse.shopping.presentation.view.order

import woowacourse.shopping.presentation.model.CardModel
import woowacourse.shopping.presentation.model.CartProductModel
import woowacourse.shopping.presentation.model.PointModel

interface OrderContract {
    interface View {
        fun setLayoutVisibility()
        fun setProductItemsView(products: List<CartProductModel>)
        fun setCardItemsView(cards: List<CardModel>)
        fun setUserPointView(userPoint: PointModel)
        fun setUsePointView(usePoint: PointModel)
        fun setPointTextChangeListener(orderPrice: Int, userPoint: PointModel)
        fun setOrderPriceView(orderPrice: Int)
        fun setSavePredictionPointView(savePredictionPoint: PointModel)
        fun showOrderDetailView(orderId: Long)
        fun handleErrorView(message: String)
    }

    interface Presenter {
        fun setUsePoint(usePoint: Int)
        fun loadOrderProducts(cartIds: ArrayList<Long>)
        fun postOrder()
    }
}
