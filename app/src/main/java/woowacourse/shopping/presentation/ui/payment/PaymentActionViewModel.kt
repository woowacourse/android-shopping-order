package woowacourse.shopping.presentation.ui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.Repository
import woowacourse.shopping.presentation.ErrorType
import woowacourse.shopping.presentation.ui.EventState
import woowacourse.shopping.presentation.ui.payment.model.CouponUiModel
import woowacourse.shopping.presentation.ui.payment.model.PaymentUiModel
import woowacourse.shopping.presentation.ui.payment.model.toUiModel

class PaymentActionViewModel(
    private val repository: Repository
): ViewModel(), PaymentActionHandler {

    private val _coupons = MutableLiveData<PaymentUiModel>()
    val coupons: LiveData<PaymentUiModel> get() = _coupons

    private val _errorHandler = MutableLiveData<EventState<ErrorType>>()
    val errorHandler: LiveData<EventState<ErrorType>> get() = _errorHandler

    fun setPaymentUiModel(paymentUiModel: PaymentUiModel) {
        _coupons.value = paymentUiModel
    }
    override fun pay() {

    }

    override fun checkCoupon(couponUiModel: CouponUiModel) {
        if(_coupons.value == null) return

        _coupons.value?.couponUiModels?.map {
            if (it.id == couponUiModel.id) {
                it.copy(isChecked = !it.isChecked)
            } else {
                it
            }
        }?.let {
            _coupons.postValue(_coupons.value?.copy(couponUiModels = it))
        }
    }

    fun getTickets() = viewModelScope.launch {
        if(_coupons.value == null) return@launch

        repository.getCoupons()
            .onSuccess {
                _coupons.postValue(_coupons.value?.copy(couponUiModels = it.map { it.toUiModel() }))
            }
            .onFailure {
                _errorHandler.postValue(EventState(ErrorType.ERROR_COUPON_LOAD))
            }
    }
}