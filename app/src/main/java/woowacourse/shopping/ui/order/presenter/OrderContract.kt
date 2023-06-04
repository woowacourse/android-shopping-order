package woowacourse.shopping.ui.order.presenter

import com.example.domain.model.Coupon

interface OrderContract {
    interface View {
        fun setCoupons(coupons: List<Coupon>)
    }

    interface Presenter {
        fun fetchCoupons()
        fun postOrder()
    }
}
