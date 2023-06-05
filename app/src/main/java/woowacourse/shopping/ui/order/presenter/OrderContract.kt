package woowacourse.shopping.ui.order.presenter

import com.example.domain.model.Coupon
import woowacourse.shopping.model.CartItemsUIModel

interface OrderContract {
    interface View {
        fun setCoupons(coupons: List<Coupon>)
        fun setTotal(totalPrice: Int)
    }

    interface Presenter {
        fun fetchCoupons()
        fun postOrder()
        fun calculateTotal(selectedCoupon: Int, coupons: List<Coupon>, cartItems: CartItemsUIModel)
    }
}
