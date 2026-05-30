package woowacourse.shopping.ui.payment

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.toRoute
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.data.repository.PaymentRepository
import woowacourse.shopping.di.AppContainer
import woowacourse.shopping.model.Money
import woowacourse.shopping.model.order.Order
import woowacourse.shopping.model.order.PaymentCalculator
import woowacourse.shopping.notification.PaymentAlarmScheduler
import woowacourse.shopping.ui.common.error.ErrorMessageMapper
import woowacourse.shopping.ui.navigation.PaymentRoute
import java.time.Clock

private const val BASE_SHIPPING_FEE = 3000L

class PaymentViewModel(
    savedStateHandle: SavedStateHandle,
    private val paymentRepo: PaymentRepository,
    private val cartRepo: CartRepository,
    private val orderRepo: OrderRepository,
    private val calculator: PaymentCalculator = PaymentCalculator(),
    private val alarmScheduler: PaymentAlarmScheduler,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PaymentUiState())
    private val _events = Channel<PaymentEvent>(Channel.BUFFERED)
    private val route: PaymentRoute = savedStateHandle.toRoute()
    private val ids = route.ids

    val uiState = _uiState.asStateFlow()
    val events = _events.receiveAsFlow()

    init {
        loadOrder()
    }

    @SuppressLint("ScheduleExactAlarm")
    fun onScreenEnter() {
        alarmScheduler.cancel()
        alarmScheduler.schedule()
    }

    fun updateSelectedId(id: Long) {
        if (_uiState.value.selectedCouponId == id) {
            _uiState.update { it.copy(selectedCouponId = null) }
        } else {
            _uiState.update { it.copy(selectedCouponId = id) }
        }

        refreshOrderSummary(id)
    }

    fun pay() {
        viewModelScope.launch {
            try {
                alarmScheduler.cancel()
                orderRepo.requestOrder(ids)
                _events.send(PaymentEvent.PaySuccess)
            } catch (e: Exception) {
                handleError("pay", e, "결제를 할 수 없어요.")
            }
        }
    }

    private fun loadOrder() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val allCartItems = cartRepo.getAllCartItems().items
                val items = allCartItems.filter { ids.contains(it.id) }
                val order = Order(
                    items = items,
                    selectedCoupon = null,
                    shippingFee = Money(BASE_SHIPPING_FEE),
                )
                val payment = calculator.calculate(order, Clock.systemDefaultZone())
                val context = order.couponContext(Clock.systemDefaultZone())
                val coupons = paymentRepo.getCoupons()
                val enableCoupons = coupons.filter { it.isUsable(context) }
                _uiState.update {
                    it.copy(
                        items = items,
                        availableCoupons = enableCoupons,
                        payment = payment,
                    )
                }
            } catch (e: Exception) {
                handleError("loadOrder", e, "결제 정보를 불러오지 못 했어요.")
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun refreshOrderSummary(id: Long) {
        val coupon = _uiState.value.availableCoupons.find { it.id == id }
        val order = Order(
            items = _uiState.value.items,
            selectedCoupon = coupon,
            shippingFee = Money(BASE_SHIPPING_FEE),
        )
        val payment = calculator.calculate(order, Clock.systemDefaultZone())
        _uiState.update { it.copy(payment = payment) }
    }

    private suspend fun handleError(
        tag: String,
        e: Exception,
        defaultMessage: String,
    ) {
        if (e is CancellationException) throw e
        Log.e(TAG, "$tag 에러", e)
        val message = ErrorMessageMapper.toUserMessage(e, defaultMessage)
        _events.send(PaymentEvent.ShowSnackbar(message))
    }

    companion object {
        private const val TAG = "PaymentViewModel"

        fun provideFactory(container: AppContainer): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras,
                ): T {
                    val savedStateHandle = extras.createSavedStateHandle()

                    return PaymentViewModel(
                        savedStateHandle = savedStateHandle,
                        paymentRepo = container.paymentRepository,
                        cartRepo = container.cartRepository,
                        orderRepo = container.orderRepository,
                        alarmScheduler = container.paymentAlarmScheduler,
                    ) as T
                }
            }
    }
}
