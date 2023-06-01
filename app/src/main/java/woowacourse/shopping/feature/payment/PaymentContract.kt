package woowacourse.shopping.feature.payment

import woowacourse.shopping.model.CartProductUiModel

interface PaymentContract {
    interface View {
        fun showCartProducts(cartProducts: List<CartProductUiModel>)
    }

    interface Presenter {
        val view: View
        fun loadCartProducts(cartProductIds: List<Int>)
    }
}
