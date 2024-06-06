package woowacourse.shopping.ui.payment.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.coupon.Coupon
import woowacourse.shopping.data.coupon.CouponRepository

class PaymentViewModel(
    private val couponRepository: CouponRepository,
) : ViewModel() {
    private val _coupons: MutableLiveData<List<Coupon>> = MutableLiveData()
    val coupons: MutableLiveData<List<Coupon>> = _coupons

    init {
        loadCoupons()
    }

    private fun loadCoupons() {
        viewModelScope.launch {
            couponRepository.getCoupons().onSuccess {
                _coupons.value = it
            }
        }
    }
}
