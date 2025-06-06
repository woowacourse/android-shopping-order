package woowacourse.shopping.view.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.coupon.remote.repository.CouponRepository
import woowacourse.shopping.data.coupon.remote.repository.DefaultCouponRepository
import woowacourse.shopping.domain.coupon.Coupon

class OrderViewModel(
    private val couponRepository: CouponRepository = DefaultCouponRepository.get(),
) : ViewModel() {
    private val _orderState = MutableLiveData<OrderState>()
    val orderState: LiveData<OrderState> get() = _orderState

    private val _couponState = MutableLiveData<CouponState>()
    val couponState: LiveData<CouponState> get() = _couponState

    private lateinit var allCoupons: List<Coupon>

    init {
        viewModelScope.launch {
            allCoupons = couponRepository.getAllCoupons()
        }
    }

    fun toggleCoupon() {
    }
}
