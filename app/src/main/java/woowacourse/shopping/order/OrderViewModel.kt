package woowacourse.shopping.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.CouponRepository

class OrderViewModel(
    private val couponRepository: CouponRepository,
) : ViewModel() {
    private val _coupons = MutableLiveData<List<Coupon>>()
    val coupons: LiveData<List<Coupon>> = _coupons

    init {
        loadCoupons()
    }

    private fun loadCoupons() {
        viewModelScope.launch {
            _coupons.postValue(couponRepository.getCoupons())
        }
    }
}
