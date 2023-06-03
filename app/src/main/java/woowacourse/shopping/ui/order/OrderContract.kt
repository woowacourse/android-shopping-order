package woowacourse.shopping.ui.order

import woowacourse.shopping.ui.model.CartProductModel

interface OrderContract {
    interface Presenter {
        fun loadProducts(ids: List<Int>)

        fun loadPoints()
    }

    interface View {
        fun showProducts(products: List<CartProductModel>)

        fun showOriginalPrice(price: Int)

        fun showPoints(points: Int)
    }
}