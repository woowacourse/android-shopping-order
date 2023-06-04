package woowacourse.shopping.feature.order

import woowacourse.shopping.model.CartProductUiModel

interface OrderContract {
    interface View {
        fun showProducts(products: List<CartProductUiModel>)
        fun showDiscount(standardPrice: Int, discountAmount: Int)
        fun showNonDiscount()
        fun showPayAmountInfo(totalPrice: Int, discountAmount: Int = 0)
        fun showPayAmount(payAmount: Int)
        fun succeedInOrder(orderId: Long)
        fun failToOrder()
    }

    interface Presenter {
        fun requestProducts()
        fun order()
    }
}
