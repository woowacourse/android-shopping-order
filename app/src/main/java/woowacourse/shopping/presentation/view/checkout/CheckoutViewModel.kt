package woowacourse.shopping.presentation.view.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.CouponRepository
import woowacourse.shopping.data.repository.RepositoryProvider
import woowacourse.shopping.domain.Coupon

class CheckoutViewModel(private val couponRepository: CouponRepository) : ViewModel() {
    private val _coupons = MutableLiveData<List<Coupon>>()
    val coupons: LiveData<List<Coupon>> = _coupons

    fun loadCoupons() {
        viewModelScope.launch {
            _coupons.value = couponRepository.loadCoupons()
        }
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    val couponRepository = RepositoryProvider.couponRepository
                    return CheckoutViewModel(couponRepository) as T
                }
            }
    }
}
