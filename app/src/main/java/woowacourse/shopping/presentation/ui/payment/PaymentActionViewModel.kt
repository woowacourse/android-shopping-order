package woowacourse.shopping.presentation.ui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.CouponRepository
import woowacourse.shopping.domain.OrderRepository
import woowacourse.shopping.domain.RecentProductRepository
import woowacourse.shopping.domain.toRecentProduct
import woowacourse.shopping.presentation.ErrorType
import woowacourse.shopping.presentation.ui.EventState
import woowacourse.shopping.presentation.ui.UpdateUiModel
import woowacourse.shopping.presentation.ui.payment.model.CouponUiModel
import woowacourse.shopping.presentation.ui.payment.model.NavigateUiState
import woowacourse.shopping.presentation.ui.payment.model.PaymentUiModel
import woowacourse.shopping.presentation.ui.payment.model.toUiModel

class PaymentActionViewModel(
    private val orderRepository: OrderRepository,
    private val couponRepository: CouponRepository,
    private val recentProductRepository: RecentProductRepository
) : ViewModel(), PaymentActionHandler {

    private val _coupons = MutableLiveData<PaymentUiModel>()
    val coupons: LiveData<PaymentUiModel> get() = _coupons

    private val _errorHandler = MutableLiveData<EventState<ErrorType>>()
    val errorHandler: LiveData<EventState<ErrorType>> get() = _errorHandler

    private val _navigateHandler = MutableLiveData<EventState<NavigateUiState>>()
    val navigateHandler: LiveData<EventState<NavigateUiState>> get() = _navigateHandler

    private val updateUiModel: UpdateUiModel = UpdateUiModel()


    fun setPaymentUiModel(paymentUiModel: PaymentUiModel) {
        _coupons.value = paymentUiModel
    }

    override fun pay() = viewModelScope.launch {
        if (_coupons.value == null)
            return@launch

        _coupons.value!!.cartProducts.forEach {
            updateRecentProduct(it)
            updateUiModel.add(it.productId, it.copy(quantity = 0))
        }

        orderRepository.postOrders(
            OrderRequest(
                _coupons.value!!.cartProductIds,
            ),
        ).onSuccess {
            _navigateHandler.postValue(EventState(NavigateUiState.ToShopping(updateUiModel)))
        }.onFailure {
            _errorHandler.postValue(EventState(ErrorType.ERROR_ORDER))
        }
    }

    fun updateRecentProduct(cartProduct: CartProduct) =
        viewModelScope.launch {
            recentProductRepository.save(cartProduct.toRecentProduct().copy(quantity = 0)).onFailure {
                _errorHandler.postValue(EventState(ErrorType.ERROR_RECENT_INSERT))
            }
        }

    override fun checkCoupon(couponUiModel: CouponUiModel) {
        if (_coupons.value == null) return

        _coupons.value?.couponUiModels?.map {
            if (it.coupon.id == couponUiModel.coupon.id) {
                it.copy(isChecked = !it.isChecked)
            } else {
                it.copy(isChecked = false)
            }
        }?.let {
            _coupons.postValue(_coupons.value?.copy(couponUiModels = it))
        }
    }

    fun loadCoupons() = viewModelScope.launch {
        if (_coupons.value == null) return@launch
        couponRepository.getCoupons()
            .onSuccess {
                _coupons.postValue(_coupons.value?.copy(couponUiModels = it.filter { it.isValid(cartProducts = _coupons.value!!.cartProducts) }.map { it.toUiModel() }))
            }
            .onFailure {
                _errorHandler.postValue(EventState(ErrorType.ERROR_COUPON_LOAD))
            }
    }
}