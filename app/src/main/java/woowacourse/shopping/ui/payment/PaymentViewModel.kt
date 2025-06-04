package woowacourse.shopping.ui.payment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.launch
import woowacourse.shopping.di.UseCaseModule.getCouponsUseCase
import woowacourse.shopping.domain.model.Coupons
import woowacourse.shopping.domain.model.Coupons.Companion.EMPTY_COUPONS
import woowacourse.shopping.domain.usecase.GetCouponsUseCase

class PaymentViewModel(
    private val getCouponsUseCase: GetCouponsUseCase,
) : ViewModel() {
    private val _coupons: MutableLiveData<Coupons> = MutableLiveData(EMPTY_COUPONS)
    val coupons: LiveData<Coupons> get() = _coupons

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isError: MutableLiveData<String> = MutableLiveData()
    val isError: LiveData<String> get() = _isError

    init {
        loadCoupons()
    }

    private fun loadCoupons() {
        viewModelScope.launch {
            if (isLoading.value == true) return@launch

            _isLoading.value = true
            getCouponsUseCase()
                .onSuccess {
                    _isLoading.value = false
                    _coupons.value = it
                }.onFailure {
                    _isLoading.value = false
                    _isError.value = it.message.toString()
                    Log.e("PaymentViewModel", it.toString())
                }
        }
    }

    fun toggleCoupon(couponId: Int) {
        _coupons.value = coupons.value?.toggleCoupon(couponId)
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T =
                    PaymentViewModel(
                        getCouponsUseCase = getCouponsUseCase,
                    ) as T
            }
    }
}
