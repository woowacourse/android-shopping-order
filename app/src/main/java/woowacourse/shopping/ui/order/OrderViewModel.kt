package woowacourse.shopping.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.coupon.Coupon
import woowacourse.shopping.domain.model.cart.SelectedCartOrder
import woowacourse.shopping.data.remote.common.NetworkMonitor
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.NotificationSettingRepository
import woowacourse.shopping.di.ShoppingRepositoryProvider
import woowacourse.shopping.notification.UnpaidOrderReminderScheduler
import java.time.Clock
import java.time.format.DateTimeFormatter
import java.util.Locale

private val COUPON_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy년 M월 d일", Locale.KOREAN)

class OrderViewModel(
    private val cartRepository: CartRepository = ShoppingRepositoryProvider.cartRepository,
    private val couponRepository: CouponRepository = ShoppingRepositoryProvider.couponRepository,
    private val notificationSettingRepository: NotificationSettingRepository =
        ShoppingRepositoryProvider.notificationSettingRepository,
    private val reminderScheduler: UnpaidOrderReminderScheduler =
        ShoppingRepositoryProvider.unpaidOrderReminderScheduler,
    private val networkMonitor: NetworkMonitor = ShoppingRepositoryProvider.networkMonitor,
    private val clock: Clock = Clock.systemDefaultZone(),
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            OrderUiState(
                isReminderEnabled = notificationSettingRepository.unpaidNotificationEnabled.value,
            ),
        )
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    private val _events =
        MutableSharedFlow<OrderEvent>(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
    val events: SharedFlow<OrderEvent> = _events

    private var pendingOrder: SelectedCartOrder? = null
    private var availableCouponsById: Map<Long, Coupon> = emptyMap()
    private var selectedCouponId: Long? = null

    init {
        observeNetworkState()
        observeReminderSetting()
        observeReminderSession()
    }

    fun loadOrder(selectedCartOrder: SelectedCartOrder) {
        applyPendingOrder(selectedCartOrder)
        loadApplicableCoupons(selectedCartOrder)
    }

    private fun clearLoadedOrder() {
        pendingOrder = null
        availableCouponsById = emptyMap()
        selectedCouponId = null

        _uiState.update { currentState ->
            currentState.copy(
                coupons = emptyList(),
                priceSummary = emptyPriceSummary(),
                isOrdering = false,
                hasPendingOrder = false,
            )
        }
    }

    fun placeOrder() {
        val targetOrder = pendingOrder ?: return
        if (targetOrder.items.isEmpty() || _uiState.value.isOrdering) return

        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(isOrdering = true)
            }

            runCatching {
                cartRepository.createOrder(targetOrder.items.map { it.cartItemId })
            }.onSuccess {
                clearLoadedOrder()
                _events.emit(OrderEvent.OrderCompleted)
            }.onFailure { throwable ->
                _uiState.update { currentState ->
                    currentState.copy(isOrdering = false)
                }
                _events.emit(OrderEvent.ShowMessage(throwable.message ?: "주문에 실패했습니다."))
            }
        }
    }

    private fun applyPendingOrder(selectedCartOrder: SelectedCartOrder) {
        pendingOrder = selectedCartOrder
        availableCouponsById = emptyMap()
        selectedCouponId = null

        _uiState.update { currentState ->
            currentState.copy(
                coupons = emptyList(),
                priceSummary = OrderPriceSummaryUiModel.from(selectedCartOrder.calculatePriceSummary()),
                isOrdering = false,
                hasPendingOrder = true,
            )
        }
    }

    fun toggleCouponSelection(
        couponId: Long,
        isSelected: Boolean,
    ) {
        selectedCouponId = if (isSelected) couponId else null

        _uiState.update { currentState ->
            currentState.copy(
                coupons =
                    currentState.coupons.map { coupon ->
                        when {
                            coupon.id == couponId -> coupon.copy(isSelected = isSelected)
                            isSelected -> coupon.copy(isSelected = false)
                            else -> coupon
                        }
                    },
            )
        }
        recalculatePriceSummary()
    }

    private fun observeNetworkState() {
        viewModelScope.launch {
            networkMonitor.isNetworkConnected.collect { isConnected ->
                _uiState.update { currentState ->
                    currentState.copy(isNetworkConnected = isConnected)
                }
            }
        }
    }

    override fun onCleared() {
        reminderScheduler.cancel()
        super.onCleared()
    }

    private fun observeReminderSetting() {
        viewModelScope.launch {
            notificationSettingRepository.unpaidNotificationEnabled.collect { isEnabled ->
                _uiState.update { currentState ->
                    currentState.copy(isReminderEnabled = isEnabled)
                }
            }
        }
    }

    private fun observeReminderSession() {
        viewModelScope.launch {
            uiState
                .map { state -> state.hasPendingOrder && state.isReminderEnabled }
                .distinctUntilChanged()
                .collect { shouldSchedule ->
                    if (shouldSchedule) {
                        reminderScheduler.schedule()
                    } else {
                        reminderScheduler.cancel()
                    }
                }
        }
    }

    private fun loadApplicableCoupons(selectedCartOrder: SelectedCartOrder) {
        viewModelScope.launch {
            runCatching {
                couponRepository
                    .getCoupons()
                    .filter { coupon -> coupon.isApplicableTo(selectedCartOrder, clock) }
                    .also { coupons -> availableCouponsById = coupons.associateBy(Coupon::id) }
                    .map(::toCouponUiModel)
            }.onSuccess { coupons ->
                _uiState.update { currentState ->
                    currentState.copy(coupons = coupons)
                }
                recalculatePriceSummary()
            }.onFailure {
                availableCouponsById = emptyMap()
                selectedCouponId = null
                _uiState.update { currentState ->
                    currentState.copy(coupons = emptyList())
                }
                emitMessage("쿠폰 목록을 불러오지 못했습니다.")
            }
        }
    }

    private fun recalculatePriceSummary() {
        val order = pendingOrder ?: return
        val selectedCoupon = selectedCouponId?.let(availableCouponsById::get)

        _uiState.update { currentState ->
            currentState.copy(
                priceSummary = OrderPriceSummaryUiModel.from(order.calculatePriceSummary(selectedCoupon)),
            )
        }
    }

    private fun toCouponUiModel(coupon: Coupon): OrderCouponUiModel =
        OrderCouponUiModel(
            id = coupon.id,
            title = coupon.title,
            expirationDateText = coupon.expirationDate.format(COUPON_DATE_FORMATTER),
            minimumOrderAmountText =
                coupon.minimumOrderAmount?.let(::formatMinimumOrderAmount) ?: "없음",
            isSelected = false,
        )

    private fun formatMinimumOrderAmount(amount: Int): String = "%,d원".format(amount)

    private fun emitMessage(message: String) {
        _events.tryEmit(OrderEvent.ShowMessage(message))
    }
}
