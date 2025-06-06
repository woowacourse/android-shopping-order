package woowacourse.shopping.feature.payment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.coupon.CouponRepository
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.util.toDomain

class PaymentViewModel(
    private val couponRepository: CouponRepository,
) : ViewModel() {
    private val _coupons = MutableLiveData<List<Coupon>>()
    val coupons: LiveData<List<Coupon>> = _coupons

    init {
        getAllCoupons()
    }

    fun getAllCoupons() {
        viewModelScope.launch {
            val coupons = couponRepository.fetchAllCoupons().map { it.toDomain() }
            _coupons.postValue(coupons)
            Log.e("123451", "$coupons")
        }
    }
}
