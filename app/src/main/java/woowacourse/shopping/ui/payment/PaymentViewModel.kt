package woowacourse.shopping.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.retrofit.toApiFailure
import woowacourse.shopping.data.remote.retrofit.toUserMessage
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.CouponBenefit
import woowacourse.shopping.domain.model.ShoppingCartItem
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.PaymentReminderScheduler
import woowacourse.shopping.domain.repository.PaymentReminderSettingsRepository
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import java.time.LocalTime

class PaymentViewModel(
    private val shoppingCartRepository: ShoppingCartRepository,
    private val couponRepository: CouponRepository,
    private val paymentReminderSettingsRepository: PaymentReminderSettingsRepository,
    private val paymentReminderScheduler: PaymentReminderScheduler,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow(
            PaymentUiState(
                isPaymentReminderEnabled = paymentReminderSettingsRepository.isEnabled(),
            ),
        )
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private var hasLoadedCoupons: Boolean = false
    private var isCouponRequestInProgress: Boolean = false
    private var selectedProductIds: Set<Long> = emptySet()
    private var allShoppingCartItems: List<ShoppingCartItem> = emptyList()

    init {
        observeShoppingCartItems()
    }

    fun initialize(selectedProductIds: Set<Long>) {
        this.selectedProductIds = selectedProductIds
        rebuildUiState()
    }

    fun requestPaymentData(force: Boolean = false) {
        requestCartItems(force = force)
        requestCoupons(force = force)
    }

    fun requestCoupons(force: Boolean = false) {
        if (!force && hasLoadedCoupons) return
        if (isCouponRequestInProgress) return

        viewModelScope.launch {
            isCouponRequestInProgress = true
            try {
                runCatching {
                    couponRepository.requestCoupons()
                }.onSuccess { coupons ->
                    hasLoadedCoupons = true
                    val currentSelectedCouponId = _uiState.value.selectedCouponId
                    val normalizedSelectedCouponId =
                        coupons
                            .firstOrNull { coupon -> coupon.id == currentSelectedCouponId }
                            ?.id
                    rebuildUiState(
                        coupons = coupons,
                        selectedCouponId = normalizedSelectedCouponId,
                    )
                }
            } finally {
                isCouponRequestInProgress = false
            }
        }
    }

    fun requestCartItems(force: Boolean = false) {
        viewModelScope.launch {
            runCatching {
                shoppingCartRepository.requestCartItems(force = force)
            }
        }
    }

    fun selectCoupon(couponId: Long?) {
        val nextSelectedCouponId =
            if (_uiState.value.selectedCouponId == couponId) {
                null
            } else {
                couponId
            }
        rebuildUiState(selectedCouponId = nextSelectedCouponId)
    }

    fun setPaymentReminderEnabled(enabled: Boolean) {
        paymentReminderSettingsRepository.setEnabled(enabled)
        _uiState.update { currentState ->
            currentState.copy(
                isPaymentReminderEnabled = enabled,
            )
        }
    }

    fun syncPaymentReminder(
        selectedProductIds: Set<Long>,
        fromReminder: Boolean,
        canPostNotifications: Boolean,
    ) {
        val isPaymentReminderEnabled = _uiState.value.isPaymentReminderEnabled
        if (isPaymentReminderEnabled && !canPostNotifications) {
            setPaymentReminderEnabled(enabled = false)
            paymentReminderScheduler.cancel()
            return
        }

        if (fromReminder || selectedProductIds.isEmpty() || !isPaymentReminderEnabled) {
            paymentReminderScheduler.cancel()
            return
        }

        paymentReminderScheduler.cancel()
        paymentReminderScheduler.schedule(selectedProductIds)
    }

    fun cancelPaymentReminder() {
        paymentReminderScheduler.cancel()
    }

    private fun observeShoppingCartItems() {
        viewModelScope.launch {
            shoppingCartRepository.observeShoppingItems().collect { shoppingCartItems ->
                allShoppingCartItems = shoppingCartItems
                rebuildUiState()
            }
        }
    }

    private fun rebuildUiState(
        coupons: List<Coupon> = _uiState.value.coupons,
        selectedCouponId: Long? = _uiState.value.selectedCouponId,
    ) {
        _uiState.update { currentState ->
            val targetItems = filterSelectedShoppingCartItems(allShoppingCartItems)
            val selectedCoupon = coupons.firstOrNull { coupon -> coupon.id == selectedCouponId }
            val subtotalPrice = targetItems.sumOf { shoppingCartItem -> shoppingCartItem.getProductQuantityPrice() }
            val discountPrice = calculateCouponDiscount(coupon = selectedCoupon, items = targetItems, subtotalPrice = subtotalPrice)
            val deliveryPrice = calculateDeliveryPrice(coupon = selectedCoupon, subtotalPrice = subtotalPrice)
            val totalPrice = (subtotalPrice - discountPrice).coerceAtLeast(0) + deliveryPrice
            currentState.copy(
                shoppingCartItems = targetItems,
                coupons = coupons,
                selectedCouponId = selectedCouponId,
                subtotalPrice = subtotalPrice,
                couponDiscountPrice = discountPrice,
                deliveryPrice = deliveryPrice,
                totalPrice = totalPrice,
            )
        }
    }

    private fun filterSelectedShoppingCartItems(shoppingCartItems: List<ShoppingCartItem>): List<ShoppingCartItem> {
        if (selectedProductIds.isEmpty()) return shoppingCartItems
        return shoppingCartItems.filter { shoppingCartItem -> shoppingCartItem.product.id in selectedProductIds }
    }

    private fun calculateCouponDiscount(
        coupon: Coupon?,
        items: List<ShoppingCartItem>,
        subtotalPrice: Int,
    ): Int {
        if (coupon == null || items.isEmpty()) return 0

        return when (val benefit = coupon.benefit) {
            is CouponBenefit.AmountDiscount ->
                if (subtotalPrice >= benefit.minimumOrderAmount) {
                    benefit.discountAmount
                } else {
                    0
                }

            is CouponBenefit.BuyTwoGetOne -> {
                val requiredQuantity = benefit.requiredQuantity + benefit.freeQuantity
                val highestEligibleProductPrice =
                    items
                        .filter { shoppingCartItem -> shoppingCartItem.getQuantity() >= requiredQuantity }
                        .maxOfOrNull { shoppingCartItem -> shoppingCartItem.product.getPrice() }

                if (highestEligibleProductPrice == null) {
                    0
                } else {
                    highestEligibleProductPrice * benefit.freeQuantity
                }
            }

            is CouponBenefit.FreeShipping -> 0

            is CouponBenefit.MorningDiscount ->
                if (isCurrentTimeWithin(start = benefit.startTime, end = benefit.endTime)) {
                    (subtotalPrice * benefit.discountRate) / paymentPricingPolicy.percentDenominator
                } else {
                    0
                }

            is CouponBenefit.Unknown -> 0
        }.coerceAtLeast(0)
    }

    private fun calculateDeliveryPrice(
        coupon: Coupon?,
        subtotalPrice: Int,
    ): Int {
        if (subtotalPrice <= 0) return 0

        val benefit = coupon?.benefit
        if (benefit is CouponBenefit.FreeShipping && subtotalPrice >= benefit.minimumOrderAmount) {
            return 0
        }
        return paymentPricingPolicy.defaultDeliveryPrice
    }

    private fun isCurrentTimeWithin(
        start: String,
        end: String,
    ): Boolean {
        val startTime = runCatching { LocalTime.parse(start) }.getOrNull() ?: return false
        val endTime = runCatching { LocalTime.parse(end) }.getOrNull() ?: return false
        val currentTime = LocalTime.now()
        return !currentTime.isBefore(startTime) && currentTime.isBefore(endTime)
    }

    data class PaymentUiState(
        val isPaymentReminderEnabled: Boolean = true,
        val shoppingCartItems: List<ShoppingCartItem> = emptyList(),
        val coupons: List<Coupon> = emptyList(),
        val selectedCouponId: Long? = null,
        val subtotalPrice: Int = 0,
        val couponDiscountPrice: Int = 0,
        val deliveryPrice: Int = 0,
        val totalPrice: Int = 0,
    )

}
