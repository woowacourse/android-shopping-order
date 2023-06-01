package woowacourse.shopping.ui.order

import woowacourse.shopping.ui.order.uistate.OrderResultUIState
import woowacourse.shopping.ui.products.uistate.ProductUIState

interface OrderResultContract {
    interface Presenter {
        fun onLoadOrder(orderId: Long)

        fun onLoadProduct(productId: Long)
    }
    interface View {
        fun showOrder(order: OrderResultUIState)

        fun showProduct(product: ProductUIState)
    }
}
