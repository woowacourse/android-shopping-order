package woowacourse.shopping.feature.order

import woowacourse.shopping.model.CartProductUiModel

interface OrderContract {
    interface View {
        fun showProducts(products: List<CartProductUiModel>)
        fun showDiscount(standardPrice: Int, discountAmount: Int)
        fun showNonDiscount()
        fun showFinalPrice(price: Int)
    }

    interface Presenter {
        fun requestProducts(cartIds: List<Long>)
    }
}
