package woowacourse.shopping.feature.order

import com.example.domain.model.Point
import woowacourse.shopping.model.CartProductUiModel

interface OrderContract {

    interface View {
        fun initAdapter(orderProducts: List<CartProductUiModel>)
        fun setUpView(point: Point, productsPrice: Int)
    }

    interface Presenter {
        fun loadProducts()
        fun loadPayment()
        fun orderProducts(usedPoint: Int)
    }
}
