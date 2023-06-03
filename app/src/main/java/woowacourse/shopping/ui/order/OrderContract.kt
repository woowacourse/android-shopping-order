package woowacourse.shopping.ui.order

import woowacourse.shopping.ui.model.CartProductModel

interface OrderContract {
    interface Presenter {
        fun loadProducts(ids: List<Int>)
    }

    interface View {
        fun showProducts(products: List<CartProductModel>)
    }
}