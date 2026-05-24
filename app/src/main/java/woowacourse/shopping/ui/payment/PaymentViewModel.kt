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
import woowacourse.shopping.domain.model.coupon.CouponInfos
import woowacourse.shopping.domain.model.order.Order
import woowacourse.shopping.domain.model.order.Payment
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.ui.event.UiEvent

class PaymentViewModel(
    private val cartRepository: CartRepository,
    private val selectedCartItemIds: List<Long>,
    val coupons: List<Coupon> = CouponInfos.defaultCoupons,
) : ViewModel() {
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    private val _payment = MutableStateFlow(Payment(selectedCoupon = coupons.firstOrNull()))
    val payment: StateFlow<Payment> = _payment.asStateFlow()

    init {
        fetchOrder()
    }

    fun selectCoupon(couponCode: String) {
        _payment.update { payment ->
            val selectedCoupon =
                if (payment.selectedCoupon?.code == couponCode) {
                    null
                } else {
                    coupons.firstOrNull { it.code == couponCode }
                }
            payment.copy(selectedCoupon = selectedCoupon)
        }
    }

    fun completePayment(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                selectedCartItemIds.forEach { cartItemId ->
                    cartRepository.deleteCartItem(cartItemId)
                }
                _uiEvent.emit(UiEvent.ShowMessage("주문이 완료되었습니다."))
                onSuccess()
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowMessage("주문에 실패했습니다. 다시 시도해주세요."))
            }
        }
    }

    private fun fetchOrder() {
        viewModelScope.launch {
            try {
                val cartItems = cartRepository.getAllCartItems()
                _payment.update { payment ->
                    payment.copy(
                        order = Order.fromSelectedCartItems(
                            cartItems = cartItems,
                            selectedCartItemIds = selectedCartItemIds,
                        ),
                    )
                }
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowMessage("주문 금액을 불러오지 못했습니다."))
            }
        }
    }
}

class PaymentViewModelFactory(
    private val cartRepository: CartRepository,
    private val selectedCartItemIds: List<Long>,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PaymentViewModel(
                cartRepository = cartRepository,
                selectedCartItemIds = selectedCartItemIds,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
