package woowacourse.shopping.presentation.payment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.payment.model.Coupon
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.presentation.base.BaseViewModelFactory

class PaymentViewModel(
    private val couponRepository: CouponRepository,
) : ViewModel() {
    private val _price: MutableLiveData<Int> = MutableLiveData()
    val price: LiveData<Int> get() = _price

    private val _coupons: MutableLiveData<List<Coupon>> = MutableLiveData()
    val coupons: LiveData<List<Coupon>> get() = _coupons

    init {
        loadCoupons()
    }

    private fun loadCoupons() {
        viewModelScope.launch {
            couponRepository.loadCoupons()
                .onSuccess {
                    _coupons.value =
                        it.map { couponData ->
                            Coupon.of(couponData)
                        }
                    Log.d("alsong", "loadCoupons: ${_coupons.value}")
                }.onFailure {
                    Log.d("alsong", "쿠폰 불러오기 실패")
                }
        }
    }

    companion object {
        fun factory(repository: CouponRepository): ViewModelProvider.Factory {
            return BaseViewModelFactory { PaymentViewModel(repository) }
        }
    }
}
