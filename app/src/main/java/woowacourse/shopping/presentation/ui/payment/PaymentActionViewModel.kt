package woowacourse.shopping.presentation.ui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.dto.request.OrderRequestDto
import woowacourse.shopping.domain.Repository
import woowacourse.shopping.presentation.ErrorType
import woowacourse.shopping.presentation.ui.EventState
import woowacourse.shopping.presentation.ui.payment.model.CouponUiModel
import woowacourse.shopping.presentation.ui.payment.model.NavigateUiState
import woowacourse.shopping.presentation.ui.payment.model.PaymentUiModel
import woowacourse.shopping.presentation.ui.payment.model.toUiModel

class PaymentActionViewModel(
    private val repository: Repository
) : ViewModel(), PaymentActionHandler {

    private val _coupons = MutableLiveData<PaymentUiModel>()
    val coupons: LiveData<PaymentUiModel> get() = _coupons

    private val _errorHandler = MutableLiveData<EventState<ErrorType>>()
    val errorHandler: LiveData<EventState<ErrorType>> get() = _errorHandler

    private val _navigateHandler = MutableLiveData<EventState<NavigateUiState>>()
    val navigateHandler: LiveData<EventState<NavigateUiState>> get() = _navigateHandler

    fun setPaymentUiModel(paymentUiModel: PaymentUiModel) {
        _coupons.value = paymentUiModel
    }

    override fun pay() = viewModelScope.launch {
        if (_coupons.value == null)
            return@launch

        repository.postOrders(
            OrderRequestDto(
                _coupons.value!!.cartProductIds,
            ),
        ).onSuccess {
            _navigateHandler.postValue(EventState(NavigateUiState.ToShopping))
        }.onFailure {
            _errorHandler.postValue(EventState(ErrorType.ERROR_ORDER))
        }
    }

    override fun checkCoupon(couponUiModel: CouponUiModel) {
        if (_coupons.value == null) return

        _coupons.value?.couponUiModels?.map {
            if (it.coupon.id == couponUiModel.coupon.id) {
                it.copy(isChecked = !it.isChecked)
            } else {
                it
            }
        }?.let {
            _coupons.postValue(_coupons.value?.copy(couponUiModels = it))
        }
    }

    fun loadCoupons() = viewModelScope.launch {
        if (_coupons.value == null) return@launch
        repository.getCoupons()
            .onSuccess {
                _coupons.postValue(_coupons.value?.copy(couponUiModels = it.filter { it.isValid(cartProducts = _coupons.value!!.cartProducts) }.map { it.toUiModel() }))
            }
            .onFailure {
                _errorHandler.postValue(EventState(ErrorType.ERROR_COUPON_LOAD))
            }
    }
}