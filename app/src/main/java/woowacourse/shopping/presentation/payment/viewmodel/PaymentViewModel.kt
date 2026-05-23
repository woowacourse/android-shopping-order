package woowacourse.shopping.presentation.payment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.di.RepositoryProvider
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.PaymentItems
import woowacourse.shopping.domain.model.PriceModifiers
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.presentation.navigation.OrderItem
import woowacourse.shopping.presentation.payment.model.PaymentUiState
import woowacourse.shopping.presentation.payment.model.toUiModel
import java.time.LocalDateTime

class PaymentViewModel(
    private val productRepository: ProductRepository = RepositoryProvider.productRepository,
    private val couponRepository: CouponRepository = RepositoryProvider.couponRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<PaymentEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    private var paymentItems: PaymentItems? = null
    private var availableCouponsDomain: List<Coupon> = emptyList()

    fun loadAvailableCoupons(orderItems: List<OrderItem>) {
        viewModelScope.launch {
            runCatching {
                val items = buildPaymentItems(orderItems)
                val now = LocalDateTime.now()
                val coupons =
                    couponRepository
                        .getCoupons()
                        .filter { it.isApplicable(items, now) }
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
            }.onFailure {
                _uiEvents.emit(PaymentEvent.ShowError("쿠폰을 불러오지 못했습니다."))
            }
        }
    }

    fun selectCoupon(couponId: Long) {
        val items = paymentItems ?: return

        val newSelectedId = if (uiState.value.selectedCouponId == couponId) null else couponId
        val selected = newSelectedId?.let { id -> availableCouponsDomain.find { it.id == id } }
        val orderPricing = calculateAmounts(selected, items)

        _uiState.update {
            it.copy(
                selectedCouponId = if (it.selectedCouponId == couponId) null else couponId,
                discountAmount = orderPricing.discountAmount,
                deliveryFee = orderPricing.deliveryFee,
                totalAmount = orderPricing.totalAmount,
            )
        }
    }

    private fun calculateAmounts(
        coupon: Coupon?,
        items: PaymentItems,
    ): PriceModifiers =
        when (coupon) {
            null ->
                PriceModifiers(
                    orderAmount = items.totalPrice.amount,
                    discountAmount = 0L,
                    deliveryFee = DEFAULT_DELIVERY_FEE,
                )
            is Coupon.FreeShipping ->
                PriceModifiers(
                    orderAmount = items.totalPrice.amount,
                    discountAmount = 0L,
                    deliveryFee = 0,
                )
            else ->
                PriceModifiers(
                    orderAmount = items.totalPrice.amount,
                    discountAmount = coupon.discountAmount(items).amount,
                    deliveryFee = DEFAULT_DELIVERY_FEE,
                )
        }

    private suspend fun buildPaymentItems(items: List<OrderItem>): PaymentItems =
        coroutineScope {
            val cartItems =
                items
                    .map { item ->
                        async {
                            CartItem(
                                id = item.cartItemId,
                                product = productRepository.getProductById(item.productId),
                                quantity = item.quantity,
                            )
                        }
                    }.awaitAll()
            PaymentItems(cartItems.toSet())
        }

    companion object {
        private const val DEFAULT_DELIVERY_FEE = 3000
    }
}

sealed interface PaymentEvent {
    data class ShowError(
        val message: String,
    ) : PaymentEvent
}
