package woowacourse.shopping.feature.order.detail

import woowacourse.shopping.model.OrderProductUiModel

interface OrderDetailContract {
    interface View {
        fun setOrderProductsInfo(list: List<OrderProductUiModel>)
        fun setOrderDate(orderDate: String)
        fun setOrderPaymentInfo(saleBeforePrice: String, saleAmount: String, saleAfterPrice: String)
    }

    interface Presenter {
        fun loadOrderInfo()
    }
}
