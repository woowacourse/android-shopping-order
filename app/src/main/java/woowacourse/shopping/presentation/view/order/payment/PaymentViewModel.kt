package woowacourse.shopping.presentation.view.order.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import woowacourse.shopping.di.provider.RepositoryProvider
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.presentation.model.CouponUiModel
import woowacourse.shopping.presentation.model.toUiModel

class PaymentViewModel(
    private val couponRepository: CouponRepository,
) : ViewModel() {
    private val _coupons = MutableLiveData<List<CouponUiModel>>()
    val coupons: LiveData<List<CouponUiModel>> = _coupons

    private val _selectedCoupon = MutableLiveData<Coupon>()
    val selectedCoupon = _selectedCoupon

    init {
        viewModelScope.launch {
            loadCoupons()
        }
    }

    private suspend fun loadCoupons() {
        couponRepository
            .fetchCoupons()
            .onSuccess { coupons ->
                _coupons.value =
                    coupons.map { coupon ->
                        coupon.toUiModel(isSelected = false)
                    }
            }.onFailure {
            }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    val couponRepository = RepositoryProvider.couponRepository
                    return PaymentViewModel(couponRepository) as T
                }
            }
    }
}
