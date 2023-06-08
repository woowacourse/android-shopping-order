package woowacourse.shopping.feature.order

import woowacourse.shopping.model.CartProductUiModel

interface OrderContract {

    interface View {
        fun initOrderProducts(orderProducts: List<CartProductUiModel>)
        fun setUpView(point: Int, productsPrice: Int)
        fun overOwnPoint(productsPrice: Int)
        fun updateTotalPriceBtn(totalPrice: Int)
        fun showErrorMessage(t: Throwable)
        fun successOrder()
    }

    interface Presenter {
        fun loadProducts()
        fun loadPayment()
        fun validatePointCondition(inputValue: CharSequence?, point: Int)
        fun orderProducts(usedPoint: Int)
    }
}
