package woowacourse.shopping.view.order.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.cart.ShoppingCart
import woowacourse.shopping.domain.coupon.CouponApplierFactory
import woowacourse.shopping.domain.coupon.CouponValidate
import woowacourse.shopping.domain.exception.onFailure
import woowacourse.shopping.domain.exception.onSuccess
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.view.cart.CartUiEvent
import woowacourse.shopping.view.core.common.withState
import woowacourse.shopping.view.core.event.MutableSingleLiveData
import woowacourse.shopping.view.core.event.SingleLiveData
import woowacourse.shopping.view.order.adapter.OrderAdapter
import woowacourse.shopping.view.order.state.OrderUiState

class OrderViewModel(
    private val couponRepository: CouponRepository,
    private val couponValidator: CouponValidate,
    private val couponFactory: CouponApplierFactory,
) : ViewModel() {
    private val _uiState = MutableLiveData<OrderUiState>()
    val uiState: LiveData<OrderUiState> get() = _uiState

    private val _uiEvent = MutableSingleLiveData<CartUiEvent>()
    val uiEvent: SingleLiveData<CartUiEvent> get() = _uiEvent

    fun loadCoupons(order: List<ShoppingCart>) {
        viewModelScope.launch {
            couponRepository.getCoupons()
                .onSuccess { result ->
                    val coupons = couponValidator.validCoupon(result, order)
                    _uiState.value = OrderUiState.of(order, coupons, DEFAULT_DELIVERY_FEE)
                }
                .onFailure(::handleFailure)
        }
    }

    private fun changeCouponCheckState(couponId: Int) {
        withState(_uiState.value) { state ->
            _uiState.value = state.changeCouponCheckState(couponId, couponFactory)
        }
    }

    private fun handleFailure(throwable: Throwable) {
        _uiEvent.setValue(CartUiEvent.ShowErrorMessage(throwable))
    }

    val couponHandler =
        object : OrderAdapter.Handler {
            override fun onChangeCheckedState(couponId: Int) {
                changeCouponCheckState(couponId)
            }
        }

    companion object {
        private const val DEFAULT_DELIVERY_FEE = 3000
    }
}
