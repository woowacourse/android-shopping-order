package woowacourse.shopping.presentation.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import woowacourse.shopping.data.repository.CartRepository
import woowacourse.shopping.di.RepositoryProvider
import woowacourse.shopping.domain.ApplyCouponUseCase
import woowacourse.shopping.domain.GetAvailableCouponUseCase
import woowacourse.shopping.domain.OrderCartItemsUseCase
import woowacourse.shopping.domain.model.CouponInfo
import woowacourse.shopping.domain.model.PaymentItems
import woowacourse.shopping.error.Result
import woowacourse.shopping.presentation.order.model.CouponUiModel
import woowacourse.shopping.presentation.order.model.OrderUiState
import woowacourse.shopping.util.formattedPrice
import java.time.format.DateTimeFormatter

sealed class OrderEvent {
    abstract val message: String

    data class Success(
        override val message: String,
    ) : OrderEvent()

    data class Fail(
        override val message: String,
    ) : OrderEvent()
}

class OrderViewModel(
    private val cartRepository: CartRepository = RepositoryProvider.cartRepository,
    private val getAvailableCouponUseCase: GetAvailableCouponUseCase = GetAvailableCouponUseCase(RepositoryProvider.orderRepository),
    private val applyCouponUseCase: ApplyCouponUseCase = ApplyCouponUseCase(RepositoryProvider.orderRepository),
    private val orderCartItemsUseCase: OrderCartItemsUseCase = OrderCartItemsUseCase(RepositoryProvider.orderRepository),
) : ViewModel() {
    private val cart = cartRepository.cart
    private val paymentItemIds = MutableStateFlow(emptySet<Long>())
    private val coupons = MutableStateFlow<List<CouponInfo>>(emptyList())
    private val selectedCouponCode = MutableStateFlow<String?>(null)

    private val paymentItems =
        combine(paymentItemIds, cart) { ids, cart ->
            PaymentItems(cart.items.filter { it.product.id in ids }.toSet())
        }

    val uiState =
        combine(paymentItems, coupons, selectedCouponCode) { paymentItems, coupons, selectedCode ->
            val discountedOrder = applyCouponUseCase(paymentItems, selectedCode)
            val selectedCoupon = coupons.find { it.code == selectedCode }
            OrderUiState(
                totalPrice = paymentItems.totalPrice,
                discountAmount = discountedOrder.discountAmount,
                deliveryFee = discountedOrder.deliveryFee.price,
                finalPrice = discountedOrder.totalAmount,
                coupons = coupons.map { it.toUiModel() },
                selectedCoupon = selectedCoupon?.toUiModel(),
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = OrderUiState(),
        )

    private val _event = MutableSharedFlow<OrderEvent>()
    val event: SharedFlow<OrderEvent> = _event.asSharedFlow()

    init {
        viewModelScope.launch {
            cartRepository.loadCart()
        }
    }

    fun initializePaymentItems(productIds: List<Long>) {
        paymentItemIds.value = productIds.toSet()
        viewModelScope.launch {
            coupons.value = getAvailableCouponUseCase(paymentItems.first())
        }
    }

    fun selectCoupon(code: String) {
        selectedCouponCode.value = if (selectedCouponCode.value == code) null else code
    }

    fun orderCartItems() {
        viewModelScope.launch {
            val itemIds = paymentItems.first().getItems().map { it.id }
            val orderResult = orderCartItemsUseCase(itemIds)
            when (orderResult) {
                is Result.Error<*, *> -> _event.emit(OrderEvent.Fail("결제 주문이 실패했습니다"))
                is Result.Success<*, *> -> _event.emit(OrderEvent.Success("결제가 성공했습니다"))
            }
        }
    }

    private fun CouponInfo.toUiModel(): CouponUiModel =
        CouponUiModel(
            code = code,
            description =
                when (this) {
                    is CouponInfo.Fixed -> "${formattedPrice(discountAmount)} 할인 쿠폰"
                    is CouponInfo.Percentage -> "$discountRate% 할인 쿠폰"
                    is CouponInfo.BuyXGetY -> "${buyQuantity}개 구매 시 ${freeGetQuantity}개 무료 쿠폰"
                    is CouponInfo.FreeShipping -> "무료 배송 쿠폰"
                },
            expirationDate = expirationDate.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일")),
            minimumOrderAmount = minimumOrderAmount,
        )
}
