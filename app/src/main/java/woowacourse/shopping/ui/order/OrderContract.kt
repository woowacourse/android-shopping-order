package woowacourse.shopping.ui.order

import woowacourse.shopping.ui.order.uistate.OrderUIState
import woowacourse.shopping.ui.products.uistate.ProductUIState

interface OrderContract {
    interface Presenter {
        fun onLoadOrder(orderId: Long)

        fun onLoadProduct(productId: Long)
    }
    interface View {
        fun showOrder(order: OrderUIState)

        fun showProduct(product: ProductUIState)
    }
}
