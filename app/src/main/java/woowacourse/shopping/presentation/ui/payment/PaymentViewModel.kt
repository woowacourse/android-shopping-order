package woowacourse.shopping.presentation.ui.payment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.Repository
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.presentation.ui.EventState
import woowacourse.shopping.presentation.ui.UiState

class PaymentViewModel(
    private val repository: Repository,
    private val ids: List<Long>,
) : ViewModel(), CouponActionHandler {
    private val _coupons = MutableLiveData<UiState<List<Coupon>>>(UiState.Loading)
    val coupons: LiveData<UiState<List<Coupon>>> = _coupons

    private val _errorHandler = MutableLiveData<EventState<String>>()
    val errorHandler: LiveData<EventState<String>> get() = _errorHandler


    fun getCoupons() = viewModelScope.launch {
        repository.getCoupons()
            .onSuccess {
                _coupons.value = UiState.Success(it)
                Log.d("PaymentViewModel", "getCoupons: ${it}")
            }.onFailure {
                Log.d("PaymentViewModel", "getCoupons: ${it}")
                _errorHandler.value = EventState(COUPON_LOAD_ERROR)
            }
    }

    override fun order() {

    }

    companion object {
        const val COUPON_LOAD_ERROR = "LOAD ERROR"
    }
}
