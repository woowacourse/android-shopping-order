package woowacourse.shopping.ui.order

import woowacourse.shopping.ui.model.CartProductModel

interface OrderContract {
    interface Presenter {
        fun loadProducts(ids: List<Int>)

        fun loadPoints()

        fun useAllPoints()
    }

    interface View {
        fun showProducts(products: List<CartProductModel>)

        fun showOriginalPrice(price: Int)

        fun showPoints(points: Int)

        fun updatePointsUsed(points: Int)

        fun updateDiscountPrice(price: Int)

        fun updateFinalPrice(price: Int)
    }
}