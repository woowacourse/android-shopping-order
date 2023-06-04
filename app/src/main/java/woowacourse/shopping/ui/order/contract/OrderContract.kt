package woowacourse.shopping.ui.order.contract

import woowacourse.shopping.model.CartProductUIModel

interface OrderContract {
    interface View {
        fun setUpOrder(cartProducts: List<CartProductUIModel>)
        fun setPrice(price: Int)
        fun setCoupons(coupon: List<String>)
        fun navigateToOrderDetail(id: Long)
    }

    interface Presenter {
        fun getOrder()
        fun getOriginalPrice()
        fun getCoupons()
        fun getTotalPrice(couponName: String)
        fun navigateToOrderDetail()
    }
}
