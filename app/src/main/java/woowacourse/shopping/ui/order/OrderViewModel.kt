package woowacourse.shopping.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.model.Coupon
import woowacourse.shopping.R
import woowacourse.shopping.network.NetworkMonitor
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.repository.CouponRepository
import woowacourse.shopping.repository.PendingOrderRepository
import woowacourse.shopping.repository.ShoppingRepositoryProvider
import woowacourse.shopping.ui.cart.SelectedCartOrder
import java.time.Clock
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val DEFAULT_DELIVERY_FEE = 3_000L
private val COUPON_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy년 M월 d일", Locale.KOREAN)

class OrderViewModel(
    private val cartRepository: CartRepository = ShoppingRepositoryProvider.cartRepository,
    private val couponRepository: CouponRepository = ShoppingRepositoryProvider.couponRepository,
    private val pendingOrderRepository: PendingOrderRepository = ShoppingRepositoryProvider.pendingOrderRepository,
    private val networkMonitor: NetworkMonitor = ShoppingRepositoryProvider.networkMonitor,
    private val clock: Clock = Clock.systemDefaultZone(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(OrderUiState())
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
    }

    fun startOrder(selectedCartOrder: SelectedCartOrder) {
        pendingOrderRepository.savePendingOrder(selectedCartOrder)
        applyPendingOrder(selectedCartOrder)
        loadApplicableCoupons(selectedCartOrder)
    }

    fun restorePendingOrderIfAvailable(): Boolean {
        val restoredOrder = pendingOrderRepository.getPendingOrder() ?: return false
        applyPendingOrder(restoredOrder)
        loadApplicableCoupons(restoredOrder)
        return true
    }

    fun clearPendingOrderSession() {
        pendingOrder = null
        pendingOrderRepository.clearPendingOrder()
        availableCouponsById = emptyMap()
        selectedCouponId = null

        _uiState.update { currentState ->
            currentState.copy(
                coupons = emptyList(),
                priceSummary = createEmptyPriceSummary(),
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
                clearPendingOrderSession()
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
                priceSummary = selectedCartOrder.toPriceSummary(),
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

    private fun loadApplicableCoupons(selectedCartOrder: SelectedCartOrder) {
        viewModelScope.launch {
            runCatching {
                couponRepository
                    .getCoupons()
                    .filter { coupon -> coupon.isApplicable(selectedCartOrder) }
                    .also { coupons -> availableCouponsById = coupons.associateBy(Coupon::id) }
                    .map { coupon -> coupon.toUiModel() }
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
                priceSummary = order.toPriceSummary(selectedCoupon),
            )
        }
    }

    private fun SelectedCartOrder.toPriceSummary(selectedCoupon: Coupon? = null): OrderPriceSummaryUiModel {
        val orderAmount = items.sumOf { it.price.toLong() * it.quantity }
        val couponDiscount = selectedCoupon.resolveDiscount(this).coerceAtMost(orderAmount)
        val deliveryFee = selectedCoupon.resolveDeliveryFee(orderAmount)
        val totalPaymentPrice = (orderAmount - couponDiscount + deliveryFee).coerceAtLeast(0)

        return OrderPriceSummaryUiModel(
            items =
                listOf(
                    OrderPriceLineUiModel(
                        labelResId = R.string.order_price_label_order_amount,
                        price = orderAmount,
                    ),
                    OrderPriceLineUiModel(
                        labelResId = R.string.order_price_label_coupon_discount,
                        price = -couponDiscount,
                    ),
                    OrderPriceLineUiModel(
                        labelResId = R.string.order_price_label_delivery_fee,
                        price = deliveryFee,
                    ),
                ),
            totalPaymentPrice = totalPaymentPrice,
        )
    }

    private fun Coupon.isApplicable(selectedCartOrder: SelectedCartOrder): Boolean {
        val today = LocalDate.now(clock)
        if (expirationDate.isBefore(today)) return false

        val orderAmount = selectedCartOrder.items.sumOf { it.price * it.quantity }
        if (minimumOrderAmount != null && orderAmount < minimumOrderAmount) return false

        if (bogoEligible) {
            val requiredQuantity = requiredSameProductQuantity ?: return false
            val hasEnoughSameProduct =
                selectedCartOrder.items.any { item ->
                    item.quantity >= requiredQuantity
                }
            if (!hasEnoughSameProduct) return false
        }

        if (availableFromHour != null && availableToHourExclusive != null) {
            val currentHour = clock.instant().atZone(clock.zone).hour
            if (currentHour !in availableFromHour until availableToHourExclusive) return false
        }

        return true
    }

    private fun Coupon.toUiModel(): OrderCouponUiModel =
        OrderCouponUiModel(
            id = id,
            title = title,
            expirationDateText = expirationDate.format(COUPON_DATE_FORMATTER),
            minimumOrderAmountText =
                minimumOrderAmount?.let(::formatMinimumOrderAmount) ?: "없음",
            isSelected = false,
        )

    private fun Coupon?.resolveDiscount(selectedCartOrder: SelectedCartOrder): Long {
        val coupon = this ?: return 0

        coupon.fixedDiscountAmount?.let { return it.toLong() }
        coupon.percentageDiscountRate?.let { rate ->
            return selectedCartOrder.items.sumOf { it.price.toLong() * it.quantity } * rate / 100
        }
        if (coupon.bogoEligible) {
            val requiredQuantity = coupon.requiredSameProductQuantity ?: return 0
            return selectedCartOrder.items
                .filter { item -> item.quantity >= requiredQuantity }
                .maxOfOrNull { item -> item.price.toLong() }
                ?: 0
        }

        return 0
    }

    private fun Coupon?.resolveDeliveryFee(orderAmount: Long): Long {
        if (orderAmount <= 0) return 0
        if (this?.freeShipping == true) return 0
        return DEFAULT_DELIVERY_FEE
    }

    private fun createEmptyPriceSummary(): OrderPriceSummaryUiModel =
        OrderPriceSummaryUiModel(
            items =
                listOf(
                    OrderPriceLineUiModel(
                        labelResId = R.string.order_price_label_order_amount,
                        price = 0,
                    ),
                    OrderPriceLineUiModel(
                        labelResId = R.string.order_price_label_coupon_discount,
                        price = 0,
                    ),
                    OrderPriceLineUiModel(
                        labelResId = R.string.order_price_label_delivery_fee,
                        price = 0,
                    ),
                ),
            totalPaymentPrice = 0,
        )

    private fun formatMinimumOrderAmount(amount: Int): String = "%,d원".format(amount)

    private fun emitMessage(message: String) {
        _events.tryEmit(OrderEvent.ShowMessage(message))
    }
}
