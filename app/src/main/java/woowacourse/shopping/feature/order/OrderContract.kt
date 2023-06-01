package woowacourse.shopping.feature.order

import woowacourse.shopping.model.CartProductUiModel

interface OrderContract {
    interface View {
        fun showProducts(products: List<CartProductUiModel>)
    }

    interface Presenter {
        fun requestProducts(cartIds: List<Long>)
    }
}
