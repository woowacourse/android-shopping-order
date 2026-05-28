package woowacourse.shopping.presentation.payment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.di.AppModule
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.OrderItem
import woowacourse.shopping.domain.model.PaymentItems
import woowacourse.shopping.domain.scheduler.PaymentNotificationScheduler
import woowacourse.shopping.domain.usecase.BuildPaymentItemsUseCase
import woowacourse.shopping.domain.usecase.CalculateOrderPricingUseCase
import woowacourse.shopping.domain.usecase.GetAvailableCouponsUseCase
import woowacourse.shopping.domain.usecase.PlaceOrderUseCase
import woowacourse.shopping.domain.usecase.SetPaymentPushAlarmUseCase
import woowacourse.shopping.presentation.payment.model.PaymentUiState
import woowacourse.shopping.presentation.payment.model.toUiModel

class PaymentViewModel(
    private val calculateOrderPricingUseCase: CalculateOrderPricingUseCase = AppModule.calculateOrderPricingUseCase,
    private val buildPaymentItemsUseCase: BuildPaymentItemsUseCase = AppModule.buildPaymentItemsUseCase,
    private val getAvailableCouponsUseCase: GetAvailableCouponsUseCase = AppModule.getAvailableCouponsUseCase,
    private val placeOrderUseCase: PlaceOrderUseCase = AppModule.placeOrderUseCase,
    private val setPaymentPushAlarmUseCase: SetPaymentPushAlarmUseCase = AppModule.setPaymentPushAlarmUseCase,
    private val notificationScheduler: PaymentNotificationScheduler = AppModule.paymentNotificationScheduler,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<PaymentEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    private var paymentItems: PaymentItems? = null
    private var availableCouponsDomain: List<Coupon> = emptyList()

    fun onScreenEntered(orderItems: List<OrderItem>) {
        notificationScheduler.cancel()
        loadAvailableCoupons(orderItems)
    }

    fun selectCoupon(couponId: Long) {
        val items = paymentItems ?: return

        val newSelectedId = if (uiState.value.selectedCouponId == couponId) null else couponId
        val selected = newSelectedId?.let { id -> availableCouponsDomain.find { it.id == id } }
        val orderPricing = calculateOrderPricingUseCase(selected, items)

        _uiState.update {
            it.copy(
                selectedCouponId = if (it.selectedCouponId == couponId) null else couponId,
                discountAmount = orderPricing.discountAmount,
                deliveryFee = orderPricing.deliveryFee,
                totalAmount = orderPricing.totalAmount,
            )
        }
    }

    fun submitOrder() {
        val items = paymentItems ?: return
        viewModelScope.launch {
            runCatching { placeOrderUseCase(items) }
                .onSuccess { _uiEvents.emit(PaymentEvent.OrderSuccess("주문이 완료되었습니다!")) }
                .onFailure { _uiEvents.emit(PaymentEvent.ShowError("주문 중 오류가 발생했습니다.")) }
        }
    }

    private fun loadAvailableCoupons(orderItems: List<OrderItem>) {
        viewModelScope.launch {
            runCatching {
                val items = buildPaymentItemsUseCase(orderItems)
                val coupons = getAvailableCouponsUseCase(items)
                items to coupons
            }.onSuccess { (items, coupons) ->
                paymentItems = items
                availableCouponsDomain = coupons
                val orderAmount = items.totalPrice.amount
                _uiState.update {
                    it.copy(
                        availableCoupons = coupons.map { it.toUiModel() },
                        orderAmount = orderAmount,
                        deliveryFee = DEFAULT_DELIVERY_FEE,
                        discountAmount = 0L,
                        totalAmount = orderAmount + DEFAULT_DELIVERY_FEE,
                        selectedCouponId = null,
                    )
                }
                setPaymentPushAlarmUseCase(orderItems, orderAmount)
            }.onFailure {
                _uiEvents.emit(PaymentEvent.ShowError("쿠폰을 불러오지 못했습니다."))
            }
        }
    }

    companion object {
        private const val DEFAULT_DELIVERY_FEE = 3000
    }
}

sealed interface PaymentEvent {
    data class ShowError(
        val message: String,
    ) : PaymentEvent

    data class OrderSuccess(
        val message: String,
    ) : PaymentEvent
}
