package woowacourse.shopping.presentation.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import woowacourse.shopping.di.AppContainer
import woowacourse.shopping.domain.model.PaymentItems
import woowacourse.shopping.domain.model.order.BuyXGetYCoupon
import woowacourse.shopping.domain.model.order.Coupon
import woowacourse.shopping.domain.model.order.DeliveryFee
import woowacourse.shopping.domain.model.order.DeliveryLocation
import woowacourse.shopping.domain.model.order.FixedAmountCoupon
import woowacourse.shopping.domain.model.order.FreeShippingCoupon
import woowacourse.shopping.domain.model.order.Order
import woowacourse.shopping.domain.model.order.PercentageCoupon
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.error.Result
import woowacourse.shopping.presentation.order.model.CouponUiModel
import woowacourse.shopping.presentation.order.model.OrderEvent
import woowacourse.shopping.presentation.order.model.OrderUiState
import woowacourse.shopping.route.OrderItem
import woowacourse.shopping.util.formattedPrice
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OrderViewModel(
    productIds: List<Long>,
    private val cartRepository: CartRepository = AppContainer.cartRepository,
    private val orderRepository: OrderRepository = AppContainer.orderRepository,
) : ViewModel() {
    private val cart = cartRepository.cart
    private val paymentItemIds = MutableStateFlow(productIds.toSet())
    private val coupons = MutableStateFlow<List<Coupon>>(emptyList())
    private val selectedCouponCode = MutableStateFlow<String?>(null)
    private var defaultOrder: Order? = null
    private val discountedOrder = MutableStateFlow<Order?>(null)

    private val paymentItems =
        combine(paymentItemIds, cart) { ids, cart ->
            PaymentItems(cart.items.filter { it.product.id in ids }.toSet())
        }

    val uiState =
        combine(paymentItems, coupons, selectedCouponCode, discountedOrder) { paymentItems, coupons, selectedCode, order ->
            if (order == null) return@combine OrderUiState(isLoading = true)
            val selectedCoupon = coupons.find { it.code == selectedCode }
            OrderUiState(
                isLoading = false,
                totalPrice = paymentItems.totalPrice,
                discountAmount = order.discountAmount,
                deliveryFee = order.deliveryFee.price,
                finalPrice = order.finalAmount,
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
            orderRepository.loadCoupons()
            val items = paymentItems.first()
            val order =
                Order(
                    dateTime = LocalDateTime.now(),
                    items = items,
                    deliveryFee = DeliveryFee(3_000L),
                    discountAmount = 0L,
                    deliveryLocation = DeliveryLocation.REMOTE,
                )
            defaultOrder = order
            discountedOrder.value = order
            coupons.value = orderRepository.coupons.value.filter { it.isApplicable(order) }
        }
    }

    fun selectCoupon(code: String) {
        selectedCouponCode.value = code
        val coupon = orderRepository.coupons.value.find { it.code == code } ?: return
        discountedOrder.value = coupon.apply(defaultOrder ?: return)
    }

    fun orderCartItems() {
        if (uiState.value.isLoading) return
        viewModelScope.launch {
            val itemIds = paymentItems.first().getItems().map { it.id }
            val orderResult = orderRepository.orderCartItems(itemIds)
            when (orderResult) {
                is Result.Error<*, *> -> _event.emit(OrderEvent.Fail("결제 주문이 실패했습니다"))
                is Result.Success<*, *> -> _event.emit(OrderEvent.Success("결제가 성공했습니다"))
            }
        }
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
                    else -> ""
                },
            expirationDate = expirationDate.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일")),
            minimumOrderAmount =
                when (this) {
                    is FixedAmountCoupon -> minimumAmount
                    is FreeShippingCoupon -> minimumAmount
                    else -> null
                },
        )

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val savedStateHandle = createSavedStateHandle()
                    OrderViewModel(
                        productIds = savedStateHandle.toRoute<OrderItem>().productIds,
                    )
                }
            }
    }
}
