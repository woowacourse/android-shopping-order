package woowacourse.shopping.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.order.Order
import woowacourse.shopping.domain.model.payment.Payment
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.ui.event.UiEvent

class PaymentViewModel(
    private val cartRepository: CartRepository,
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
    private val selectedCartItemIds: List<Long>,
) : ViewModel() {
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    private val _coupons = MutableStateFlow<List<Coupon>>(emptyList())
    val coupons: StateFlow<List<Coupon>> = _coupons.asStateFlow()

    private val _payment = MutableStateFlow(Payment(selectedCoupon = null))
    val payment: StateFlow<Payment> = _payment.asStateFlow()

    init {
        fetchCoupons()
        fetchOrder()
    }

    fun selectCoupon(couponCode: String) {
        _payment.update { payment ->
            val selectedCoupon =
                if (payment.selectedCoupon?.code == couponCode) {
                    null
                } else {
                    coupons.value.firstOrNull { it.code == couponCode }
                }
            payment.copy(selectedCoupon = selectedCoupon)
        }
    }

    fun completePayment(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                orderRepository.createOrder(selectedCartItemIds)
                _uiEvent.emit(UiEvent.ShowMessage("주문이 완료되었습니다."))
                onSuccess()
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowMessage("주문에 실패했습니다. 다시 시도해주세요."))
            }
        }
    }

    private fun fetchCoupons() {
        viewModelScope.launch {
            try {
                val fetchedCoupons = couponRepository.getCoupons()
                _coupons.update { fetchedCoupons }
                _payment.update { payment ->
                    payment.copy(selectedCoupon = fetchedCoupons.firstOrNull())
                }
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowMessage("쿠폰을 불러오지 못했습니다."))
            }
        }
    }

    private fun fetchOrder() {
        viewModelScope.launch {
            try {
                val cartItems = cartRepository.getAllCartItems()
                val order =
                    Order.fromSelectedCartItems(
                        cartItems = cartItems,
                        selectedCartItemIds = selectedCartItemIds,
                    )
                _payment.update { payment ->
                    payment.copy(order = order)
                }
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowMessage("주문 금액을 불러오지 못했습니다."))
            }
        }
    }
}

class PaymentViewModelFactory(
    private val cartRepository: CartRepository,
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
    private val selectedCartItemIds: List<Long>,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PaymentViewModel(
                cartRepository = cartRepository,
                couponRepository = couponRepository,
                orderRepository = orderRepository,
                selectedCartItemIds = selectedCartItemIds,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
