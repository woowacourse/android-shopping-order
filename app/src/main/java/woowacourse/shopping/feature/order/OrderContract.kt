package woowacourse.shopping.feature.order

import woowacourse.shopping.model.CartState

interface OrderContract {
    interface View {
        fun setOrderProducts(orderProducts: CartState)
        fun setProductsSum(productsSum: Int)
        fun setDiscountPrice(discountPrice: Int)
        fun setFinalPrice(finalPrice: Int)
        fun showOrderDetailPage(orderId: Long)
    }

    interface Presenter {
        fun loadOrderProducts()
        fun calculatePrice()
        fun navigateToOrderDetail()
    }
}
