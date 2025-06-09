package woowacourse.shopping.view.order.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.cart.ShoppingCarts
import woowacourse.shopping.domain.coupon.CouponApplierFactory
import woowacourse.shopping.domain.coupon.CouponValidate
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.view.core.common.withState
import woowacourse.shopping.view.core.event.MutableSingleLiveData
import woowacourse.shopping.view.core.event.SingleLiveData
import woowacourse.shopping.view.order.OrderUiEvent
import woowacourse.shopping.view.order.adapter.OrderAdapter
import woowacourse.shopping.view.order.state.OrderUiState
import java.time.LocalDateTime

class OrderViewModel(
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
    private val couponValidator: CouponValidate,
    private val couponFactory: CouponApplierFactory,
) : ViewModel() {
    private val _uiState = MutableLiveData<OrderUiState>()
    val uiState: LiveData<OrderUiState> get() = _uiState

    private val _uiEvent = MutableSingleLiveData<OrderUiEvent>()
    val uiEvent: SingleLiveData<OrderUiEvent> get() = _uiEvent

    fun loadCoupons(order: ShoppingCarts) {
        viewModelScope.launch {
            couponRepository.getCoupons()
                .onSuccess { result ->
                    val now = LocalDateTime.now()
                    val coupons = couponValidator.validCoupon(now, result, order)
                    _uiState.value = OrderUiState.of(order, coupons, DEFAULT_DELIVERY_FEE)
                }
                .onFailure(::handleFailure)
        }
    }

    fun sendOrder() {
        viewModelScope.launch {
            withState(_uiState.value) { state ->
                orderRepository.createOrder(state.orderCartIds)
                    .onSuccess { _uiEvent.setValue(OrderUiEvent.OrderComplete) }
                    .onFailure(::handleFailure)
            }
        }
    }

    private fun changeCouponCheckState(couponId: Int) {
        withState(_uiState.value) { state ->
            _uiState.value = state.changeCouponCheckState(couponId, couponFactory)
        }
    }

    private fun handleFailure(throwable: Throwable) {
        _uiEvent.setValue(OrderUiEvent.ShowErrorMessage(throwable))
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
