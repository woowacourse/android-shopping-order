package woowacourse.shopping.ui.payment

import android.util.Log
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
import retrofit2.HttpException
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.CouponRepository
import woowacourse.shopping.data.repository.NotificationRepository
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.di.AppContainer
import woowacourse.shopping.notification.PaymentAlarmScheduler

class PaymentViewModel(
    private val cartItemIds: List<Long>,
    private val cartRepository: CartRepository,
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
    private val notificationRepository: NotificationRepository,
    private val paymentAlarmScheduler: PaymentAlarmScheduler,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<PaymentEvent>()
    val event: SharedFlow<PaymentEvent> = _event.asSharedFlow()

    init {
        paymentAlarmScheduler.cancel(cartItemIds)
        if (notificationRepository.isEnabled()) {
            paymentAlarmScheduler.schedule(cartItemIds)
        }
        loadPayment()
    }

    fun selectCoupon(couponId: Long) {
        _uiState.update { state ->
            val coupon = state.coupons.firstOrNull { it.id == couponId } ?: return@update state
            val discountAmount = coupon.applicableDiscount(items = state.items, shippingFee = state.shippingFee)
            if (discountAmount <= 0) return@update state

            state.copy(selectedCouponId = if (state.selectedCouponId == couponId) null else couponId)
        }
    }

    fun pay() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                orderRepository.requestOrder(cartItemIds)
                paymentAlarmScheduler.cancel(cartItemIds)
                _event.emit(PaymentEvent.PaymentSuccess)
                Log.d("order", "주문에 성공했습니다")
            } catch (e: HttpException) {
                Log.d("order", "주문에 실패했습니다 ${e.message}")
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun loadPayment() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val selectedItems =
                    cartRepository
                        .getAllCartItems()
                        .items
                        .filter { it.id in cartItemIds }
                _uiState.update {
                    it.copy(
                        items = selectedItems,
                        coupons = couponRepository.getCoupons(),
                    )
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    companion object {
        fun provideFactory(
            container: AppContainer,
            cartItemIds: List<Long>,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    PaymentViewModel(
                        cartItemIds = cartItemIds,
                        cartRepository = container.cartRepository,
                        couponRepository = container.couponRepository,
                        orderRepository = container.orderRepository,
                        notificationRepository = container.notificationRepository,
                        paymentAlarmScheduler = container.paymentAlarmScheduler,
                    ) as T
            }
    }
}

sealed interface PaymentEvent {
    data object PaymentSuccess : PaymentEvent
}
