package woowacourse.shopping.presentation.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.R
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.presentation.SingleLiveData
import woowacourse.shopping.presentation.model.CouponUiModel
import woowacourse.shopping.presentation.model.toPresentation

class OrderViewModel(
    private val couponRepository: CouponRepository,
) : ViewModel() {
    private val _coupons: MutableLiveData<List<CouponUiModel>> = MutableLiveData()
    val coupons: LiveData<List<CouponUiModel>> = _coupons
    private val _toastMessage = SingleLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            couponRepository
                .fetchAll()
                .onSuccess { coupons ->
                    Log.d("meeple_log", "$coupons")
                    _coupons.value = coupons.map { it.toPresentation() }
                }.onFailure {
                    _toastMessage.value = R.string.order_toast_coupon_load_fail
                }
        }
    }
}
