package woowacourse.shopping.feature.order

import woowacourse.shopping.model.CartProductUiModel

interface OrderContract {

    interface View {
        fun initAdapter(orderProducts: List<CartProductUiModel>)
        fun setUpView(point: Int, productsPrice: Int)
    }

    interface Presenter {
        fun loadProducts()
        fun loadPayment()
        fun orderProducts(usedPoint: Int)
    }
}
