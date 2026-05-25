package woowacourse.shopping.ui.order

import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.constant.Format.formatPrice
import woowacourse.shopping.domain.cart.CartItems
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.Coupons
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.NotificationRepository
import woowacourse.shopping.notification.AlarmScheduler
import woowacourse.shopping.ui.navigation.Order
import woowacourse.shopping.ui.util.LoadState
import woowacourse.shopping.ui.util.toUiModel
import java.time.LocalDateTime

class OrderViewModel(
    savedStateHandle: SavedStateHandle,
    private val cartRepository: CartRepository,
    private val couponRepository: CouponRepository,
    private val alarmScheduler: AlarmScheduler,
    private val notificationRepository: NotificationRepository,
) : ViewModel() {
    private val route: Order = savedStateHandle.toRoute()

    private val cartIds = route.cartIds

    private val cartItems = MutableStateFlow(CartItems())
    private val availableCoupons = MutableStateFlow(Coupons())

    private val selectedCoupon = MutableStateFlow<Coupon?>(null)
    private val loadState = MutableStateFlow<LoadState>(LoadState.Initial)

    private val _events = MutableSharedFlow<OrderEvent>()
    val events: SharedFlow<OrderEvent> = _events.asSharedFlow()

    val uiState: StateFlow<OrderUiState> =
        combine(
            cartItems,
            availableCoupons,
            selectedCoupon,
            loadState,
        ) { cartItems, availableCoupons, selectedCoupon, loadState ->

            val totalPrice = cartItems.totalPrice
            val discountAmount = selectedCoupon?.discount(cartItems) ?: 0
            OrderUiState(
                totalPrice = formatPrice(totalPrice),
                discountAmount = formatPrice(discountAmount),
                shippingFee = formatPrice(Coupon.SHIPPING_FEE),
                amountToPay = formatPrice(totalPrice - discountAmount),
                coupons = availableCoupons.toUiModel(selectedCoupon),
                loadState = loadState,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = OrderUiState(),
        )

    init {
        loadData()
    }

    fun onEnterScreen() {
        if (notificationRepository.isNotificationEnabled()) {
            alarmScheduler.schedule(cartIds)
        }
    }

    private fun loadData() {
        loadState.update { LoadState.Loading }
        viewModelScope.launch {
            runCatching {
                val fetchedCartItems = cartRepository.getCartItemsByIds(cartIds)
                val coupons = couponRepository.getCoupons()
                cartItems.update { fetchedCartItems }
                availableCoupons.update {
                    coupons.getApplicableCoupons(
                        LocalDateTime.now(),
                        fetchedCartItems,
                    )
                }
                loadState.update { LoadState.Success }
            }.onFailure { throwable ->
                loadState.update { LoadState.Error(throwable, throwable.message) }
            }
        }
    }

    fun selectCoupon(couponId: Int) {
        selectedCoupon.update { availableCoupons.value.getCouponById(couponId) }
    }

    fun order() {
        viewModelScope.launch {
            runCatching {
                cartRepository.order(cartItems.value.values.map { it.id })
                alarmScheduler.cancel()
                _events.emit(OrderEvent.OrderSuccess)
            }.onFailure { throwable ->
                loadState.update { LoadState.Error(throwable, throwable.message) }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val savedStateHandle = createSavedStateHandle()
                    val application =
                        (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ShoppingApplication)
                    OrderViewModel(
                        savedStateHandle = savedStateHandle,
                        cartRepository = application.appContainer.cartRepository,
                        couponRepository = application.appContainer.couponRepository,
                        alarmScheduler = application.appContainer.alarmScheduler,
                        notificationRepository = application.appContainer.notificationRepository,
                    )
                }
            }
    }
}

sealed interface OrderEvent {
    data object OrderSuccess : OrderEvent
}
