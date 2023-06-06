package woowacourse.shopping.feature.order

import woowacourse.shopping.model.CartState

interface OrderContract {
    interface View {
        fun setOrderPendingCart(orderPendingCart: CartState)
        fun setProductsSum(productsSum: Int)
        fun setDiscountPrice(discountPrice: Int)
        fun setFinalPrice(finalPrice: Int)
        fun showOrderDetailPage(orderId: Long)
    }

    interface Presenter {
        fun loadOrderPendingCart()
        fun calculatePrice()
        fun navigateToOrderDetail()
    }
}
