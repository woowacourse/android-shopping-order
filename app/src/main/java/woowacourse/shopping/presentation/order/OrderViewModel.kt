package woowacourse.shopping.presentation.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.R
import woowacourse.shopping.domain.usecase.GetAvailableCouponUseCase
import woowacourse.shopping.presentation.Extra.KEY_SELECT_PRICE
import woowacourse.shopping.presentation.SingleLiveData
import woowacourse.shopping.presentation.model.CouponUiModel
import woowacourse.shopping.presentation.model.toPresentation

class OrderViewModel(
    savedStateHandle: SavedStateHandle,
    private val getAvailableCouponUseCase: GetAvailableCouponUseCase,
) : ViewModel() {
    private val _coupons: MutableLiveData<List<CouponUiModel>> = MutableLiveData()
    val coupons: LiveData<List<CouponUiModel>> = _coupons
    private val _toastMessage = SingleLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage

    val orderPrice: LiveData<Int> = savedStateHandle.getLiveData(KEY_SELECT_PRICE, 0)

    init {
        fetchData()
    }

    private fun fetchData() {
        val orderPrice = orderPrice.value ?: return
        viewModelScope.launch {
            getAvailableCouponUseCase
                .invoke(orderPrice)
                .onSuccess { coupons ->
                    _coupons.value = coupons.map { it.toPresentation() }
                }.onFailure {
                    _toastMessage.value = R.string.order_toast_coupon_load_fail
                }
        }
    }
}
