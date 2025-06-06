package woowacourse.shopping.view.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OrderViewModel(
    private val couponRepository: CouponRepository,
) : ViewModel() {
    private val _orderState = MutableLiveData<OrderState>()
    val orderState: LiveData<OrderState> get() = _orderState

    private val _couponState = MutableLiveData<CouponState>()
    val couponState: LiveData<CouponState> get() = _couponState

    private val allCoupons = couponRepository.getAllCoupons()

    fun toggleCoupon() {
    }
}
