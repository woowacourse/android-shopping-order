
package woowacourse.shopping.feature.order.detail

import com.example.domain.order.OrderProduct
import woowacourse.shopping.model.order.OrderState

interface OrderDetailContract {
    interface View {
        fun setViewFixedContents(order: OrderState)
        fun setProductsSummary(firstProductName: String, productsCount: Int, originalPrice: Int)
        fun setOrderDate(orderDate: String)
        fun setOrderNumber(number: Long)
        fun setOrderProducts(orderProducts: List<OrderProduct>)
        fun showAccessError()
        fun closeOrderDetail()
    }

    interface Presenter {
        fun loadOrderInformation()
    }
}
