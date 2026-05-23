package woowacourse.shopping.presentation.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.data.repository.PaymentRepository
import woowacourse.shopping.di.RepositoryProvider
import woowacourse.shopping.domain.model.PaymentItems
import woowacourse.shopping.domain.model.payment.BuyXGetYCoupon
import woowacourse.shopping.domain.model.payment.Coupon
import woowacourse.shopping.domain.model.payment.DeliveryFee
import woowacourse.shopping.domain.model.payment.DeliveryLocation
import woowacourse.shopping.domain.model.payment.FixedAmountCoupon
import woowacourse.shopping.domain.model.payment.FreeShippingCoupon
import woowacourse.shopping.domain.model.payment.Order
import woowacourse.shopping.domain.model.payment.PercentageCoupon
import woowacourse.shopping.error.Result
import woowacourse.shopping.presentation.order.model.CouponUiModel
import woowacourse.shopping.presentation.order.model.OrderUiState
import woowacourse.shopping.util.formattedPrice
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OrderViewModel(
    private val cartRepository: CartRepository = RepositoryProvider.cartRepository,
    private val paymentRepository: PaymentRepository = RepositoryProvider.paymentRepository,
) : ViewModel() {
    private val cart = cartRepository.cart
    private val paymentItemIds = MutableStateFlow(emptySet<Long>())
    private val coupons = MutableStateFlow<List<Coupon>>(emptyList())
    private val selectedCoupon = MutableStateFlow<Coupon?>(null)

    val uiState =
        combine(paymentItemIds, cart, coupons, selectedCoupon) { ids, cart, coupons, selected ->
            val paymentItems = PaymentItems(cart.items.filter { it.product.id in ids }.toSet())
            val order =
                Order(
                    dateTime = LocalDateTime.now(),
                    items = paymentItems,
                    deliveryFee = DeliveryFee(3_000L),
                    discountAmount = 0L,
                    deliveryLocation = DeliveryLocation.STANDARD,
                )
            val discountedOrder =
                selected
                    ?.let { runCatching { it.apply(order) }.getOrDefault(order) }
                    ?: order
            OrderUiState(
                totalPrice = paymentItems.totalPrice,
                discountAmount = discountedOrder.discountAmount,
                deliveryFee = discountedOrder.deliveryFee.price,
                finalPrice = discountedOrder.totalAmount,
                coupons = coupons.map { it.toUiModel() },
                selectedCoupon = selected?.toUiModel(),
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = OrderUiState(),
        )

    init {
        viewModelScope.launch {
            cartRepository.loadCart()
            val result = paymentRepository.getCoupons()
            if (result is Result.Success) {
                coupons.value = result.data
            }
        }
    }

    fun initializePaymentItems(productIds: List<Long>) {
        paymentItemIds.value = productIds.toSet()
    }

    fun selectCoupon(code: String) {
        val coupon = coupons.value.find { it.code == code }
        selectedCoupon.value = if (selectedCoupon.value?.code == code) null else coupon
    }

    private fun Coupon.toUiModel(): CouponUiModel =
        CouponUiModel(
            code = code,
            description =
                when (this) {
                    is FixedAmountCoupon -> "${formattedPrice(discountAmount)} 할인 쿠폰"
                    is PercentageCoupon -> "$discountRate% 할인 쿠폰"
                    is BuyXGetYCoupon -> "${buyQuantity}개 구매 시 ${freeGetQuantity}개 무료 쿠폰"
                    is FreeShippingCoupon -> "무료 배송 쿠폰"
                    else -> "쿠폰"
                },
            expirationDate = expirationDate.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일")),
            minimumOrderAmount =
                when (this) {
                    is FixedAmountCoupon -> minimumAmount
                    is FreeShippingCoupon -> minimumAmount
                    else -> null
                },
        )
}
