package woowacourse.shopping.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.payment.PaymentReminderManager
import woowacourse.shopping.domain.payment.PaymentPriceCalculator
import woowacourse.shopping.domain.payment.PaymentPriceCalculator.PaymentPriceSummary
import woowacourse.shopping.domain.model.ShoppingCartItem
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository

class PaymentViewModel(
    private val shoppingCartRepository: ShoppingCartRepository,
    private val couponRepository: CouponRepository,
    private val paymentReminderManager: PaymentReminderManager,
    private val paymentPriceCalculator: PaymentPriceCalculator,
) : ViewModel() {
    private val _uiState: MutableStateFlow<PaymentUiState> =
        MutableStateFlow(
            PaymentUiState.Loading(
                isPaymentReminderEnabled = paymentReminderManager.isEnabled(),
            ),
        )
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private val internalState = MutableStateFlow(PaymentInternalState())

    init {
        observeShoppingCartItems()
    }

    fun initialize(selectedProductIds: Set<Long>) {
        internalState.update { currentState ->
            currentState.copy(selectedProductIds = selectedProductIds)
        }
        publishUiState()
    }

    fun requestPaymentData(force: Boolean = false) {
        requestCartItems(force = force)
        requestCoupons(force = force)
    }

    fun requestCoupons(force: Boolean = false) {
        if (!startCouponRequest(force = force)) return

        viewModelScope.launch {
            try {
                when (val couponRequestResult = couponRepository.requestCoupons()) {
                    is CouponRepository.CouponRequestResult.Success -> {
                        updateCoupons(couponRequestResult.coupons)
                        publishUiState()
                    }

                    is CouponRepository.CouponRequestResult.Failure -> {
                        // 기존 시나리오 유지: 쿠폰 요청 실패 시 별도 에러 노출 없이 현재 상태를 유지한다.
                    }
                }
            } finally {
                internalState.update { currentState ->
                    currentState.copy(isCouponRequestInProgress = false)
                }
            }
        }
    }

    fun requestCartItems(force: Boolean = false) {
        viewModelScope.launch {
            try {
                shoppingCartRepository.requestCartItems(force = force)
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (_: Exception) {
                // 기존 시나리오 유지: 장바구니 요청 실패 시 별도 에러 노출 없이 현재 상태를 유지한다.
            }
        }
    }

    fun selectCoupon(couponId: Long?) {
        internalState.update { currentState ->
            val nextSelectedCouponId =
                if (currentState.selectedCouponId == couponId) {
                    null
                } else {
                    couponId
                }
            currentState.copy(selectedCouponId = nextSelectedCouponId)
        }
        publishUiState()
    }

    fun setPaymentReminderEnabled(enabled: Boolean) {
        paymentReminderManager.setEnabled(enabled)
        _uiState.update { currentState ->
            currentState.withPaymentReminderEnabled(enabled = enabled)
        }
    }

    fun syncPaymentReminder(
        selectedProductIds: Set<Long>,
        fromReminder: Boolean,
        canPostNotifications: Boolean,
    ) {
        val effectiveEnabled =
            paymentReminderManager.synchronize(
                selectedProductIds = selectedProductIds,
                fromReminder = fromReminder,
                canPostNotifications = canPostNotifications,
            )
        if (_uiState.value.isPaymentReminderEnabled == effectiveEnabled) return
        _uiState.update { currentState ->
            currentState.withPaymentReminderEnabled(enabled = effectiveEnabled)
        }
    }

    fun cancelPaymentReminder() {
        paymentReminderManager.cancel()
    }

    private fun observeShoppingCartItems() {
        viewModelScope.launch {
            shoppingCartRepository.observeShoppingItems().collect { shoppingCartItems ->
                internalState.update { currentState ->
                    currentState.copy(
                        allShoppingCartItems = shoppingCartItems,
                        hasLoadedCartSnapshot = true,
                    )
                }
                publishUiState()
            }
        }
    }

    private fun startCouponRequest(force: Boolean): Boolean {
        while (true) {
            val currentState = internalState.value
            if (!force && currentState.hasLoadedCoupons) return false
            if (currentState.isCouponRequestInProgress) return false
            val nextState = currentState.copy(isCouponRequestInProgress = true)
            if (internalState.compareAndSet(currentState, nextState)) return true
        }
    }

    private fun updateCoupons(coupons: List<Coupon>) {
        internalState.update { currentState ->
            val normalizedSelectedCouponId =
                coupons
                    .firstOrNull { coupon -> coupon.id == currentState.selectedCouponId }
                    ?.id
            currentState.copy(
                hasLoadedCoupons = true,
                coupons = coupons,
                selectedCouponId = normalizedSelectedCouponId,
            )
        }
    }

    private fun publishUiState() {
        val currentInternalState = internalState.value
        val isPaymentReminderEnabled = _uiState.value.isPaymentReminderEnabled
        if (!currentInternalState.hasLoadedCartSnapshot) {
            _uiState.value = PaymentUiState.Loading(isPaymentReminderEnabled = isPaymentReminderEnabled)
            return
        }

        val targetItems =
            filterSelectedShoppingCartItems(
                shoppingCartItems = currentInternalState.allShoppingCartItems,
                selectedProductIds = currentInternalState.selectedProductIds,
            )
        val selectedCoupon =
            currentInternalState.coupons.firstOrNull { coupon ->
                coupon.id == currentInternalState.selectedCouponId
            }
        val paymentPriceSummary =
            paymentPriceCalculator.calculate(
                items = targetItems,
                coupon = selectedCoupon,
            )

        _uiState.value =
            PaymentUiState.Content(
                isPaymentReminderEnabled = isPaymentReminderEnabled,
                coupons = currentInternalState.coupons,
                selectedCouponId = currentInternalState.selectedCouponId,
                priceSummary = paymentPriceSummary,
            )
    }

    private fun filterSelectedShoppingCartItems(
        shoppingCartItems: List<ShoppingCartItem>,
        selectedProductIds: Set<Long>,
    ): List<ShoppingCartItem> {
        if (selectedProductIds.isEmpty()) return shoppingCartItems
        return shoppingCartItems.filter { shoppingCartItem -> shoppingCartItem.product.id in selectedProductIds }
    }

    sealed interface PaymentUiState {
        val isPaymentReminderEnabled: Boolean
        val coupons: List<Coupon>
        val selectedCouponId: Long?
        val priceSummary: PaymentPriceSummary?

        data class Loading(
            override val isPaymentReminderEnabled: Boolean,
            override val coupons: List<Coupon> = emptyList(),
            override val selectedCouponId: Long? = null,
            override val priceSummary: PaymentPriceSummary? = null,
        ) : PaymentUiState

        data class Content(
            override val isPaymentReminderEnabled: Boolean,
            override val coupons: List<Coupon>,
            override val selectedCouponId: Long?,
            override val priceSummary: PaymentPriceSummary,
        ) : PaymentUiState
    }

    private data class PaymentInternalState(
        val hasLoadedCoupons: Boolean = false,
        val isCouponRequestInProgress: Boolean = false,
        val selectedProductIds: Set<Long> = emptySet(),
        val allShoppingCartItems: List<ShoppingCartItem> = emptyList(),
        val hasLoadedCartSnapshot: Boolean = false,
        val coupons: List<Coupon> = emptyList(),
        val selectedCouponId: Long? = null,
    )

    private fun PaymentUiState.withPaymentReminderEnabled(enabled: Boolean): PaymentUiState =
        when (this) {
            is PaymentUiState.Loading ->
                copy(isPaymentReminderEnabled = enabled)

            is PaymentUiState.Content ->
                copy(isPaymentReminderEnabled = enabled)
        }

}
