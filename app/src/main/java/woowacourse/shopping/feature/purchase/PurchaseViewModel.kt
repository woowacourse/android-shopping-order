package woowacourse.shopping.feature.purchase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import java.time.LocalDateTime
import java.time.LocalTime
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.PurchaseRoute
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.repository.cart.CartRepository
import woowacourse.shopping.data.repository.coupon.CouponRepository
import woowacourse.shopping.data.repository.order.OrderRepository
import woowacourse.shopping.data.repository.setting.SettingRepository
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.CartContent
import woowacourse.shopping.domain.coupon.BuyXGetYCoupon
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.FixedDiscountCoupon
import woowacourse.shopping.domain.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.coupon.OrderContext
import woowacourse.shopping.domain.coupon.PercentageCoupon
import woowacourse.shopping.feature.notification.PaymentNotificationScheduler

data class PurchaseUiState(
    val isLoading: Boolean = false,
    val couponUiModels: List<CouponUiModel> = emptyList(),
    val selectedCouponId: String? = null,
    val originalPrice: Int = 0,
    val couponDiscountPrice: Int = 0,
    val shippingPrice: Int = 3000,
    val totalDiscountedPrice: Int = 0,
)

class PurchaseViewModel(
    private val contentIds: List<String>,
    private val originalPrice: Int,
    private val orderRepository: OrderRepository,
    private val couponRepository: CouponRepository,
    private val cartRepository: CartRepository,
    private val settingRepository: SettingRepository,
    private val paymentNotificationScheduler: PaymentNotificationScheduler,
) : ViewModel() {
    private var coupons: List<Coupon> = emptyList()
    private var cartContents: List<CartContent> = emptyList()
    private val _uiState = MutableStateFlow(PurchaseUiState())
    val uiState: StateFlow<PurchaseUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<PurchaseUiEvent>()
    val event: SharedFlow<PurchaseUiEvent> = _event.asSharedFlow()

    private var cart: Cart = Cart(emptyList())

    fun initialLoading() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            refreshCart()

            cartContents = cart.cartContents.filter { contentIds.contains(it.id) }

            reserveNotification()

            val uiModels = getCoupons()
            _uiState.update {
                it.copy(
                    originalPrice = originalPrice,
                    totalDiscountedPrice = originalPrice + uiState.value.shippingPrice,
                    couponUiModels = uiModels,
                )
            }
        }
    }

    private suspend fun reserveNotification() {
        if (settingRepository.isPaymentNotificationEnabled()) {
            paymentNotificationScheduler.scheduleAt(
                at = LocalDateTime.now().plusSeconds(10),
                contentIds = contentIds,
                totalPrice = originalPrice,
            )
        }
    }

    private suspend fun refreshCart() {
        cart = cartRepository.loadCart()
    }

    private suspend fun getCoupons(): List<CouponUiModel> {
        coupons = couponRepository.getAllCoupons()
        return coupons.map { it.toUiModel() }
    }

    fun couponSelect(couponId: String) {

        couponDiscount(coupons.first { it.id == couponId })
        _uiState.update {
            it.copy(
                selectedCouponId = couponId,
            )
        }
    }

    fun couponDiscount(coupon: Coupon) {
        var couponDiscountPrice = 0
        var shippingPrice = 3000
        var totalDiscountedPrice: Int = uiState.value.originalPrice
        when (coupon) {
            is BuyXGetYCoupon -> {
                couponDiscountPrice = coupon.discountAmount(
                    OrderContext(
                        items = cartContents,
                    ),
                )
                totalDiscountedPrice -= couponDiscountPrice
            }

            is FixedDiscountCoupon -> {
                couponDiscountPrice = coupon.discountAmount(
                    OrderContext(
                        totalPrice = totalDiscountedPrice,
                    ),
                )
                totalDiscountedPrice -= couponDiscountPrice
            }

            is FreeShippingCoupon -> {
                shippingPrice = coupon.discountAmount(
                    OrderContext(
                        totalPrice = totalDiscountedPrice,
                    ),
                )
                totalDiscountedPrice -= shippingPrice
            }

            is PercentageCoupon -> {
                couponDiscountPrice = coupon.discountAmount(
                    OrderContext(
                        totalPrice = totalDiscountedPrice,
                        now = LocalTime.now(),
                    ),
                )
                totalDiscountedPrice -= couponDiscountPrice
            }
        }
        _uiState.update {
            it.copy(
                couponDiscountPrice = couponDiscountPrice,
                shippingPrice = shippingPrice,
                totalDiscountedPrice = totalDiscountedPrice,
            )
        }
    }

    fun purchase() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            orderRepository.orders(contentIds)
            _event.emit(PurchaseUiEvent.PurchaseComplete("주문이 완료되었습니다."))
        }
    }

    fun Coupon.toUiModel(): CouponUiModel = when (this) {
        is FixedDiscountCoupon -> CouponUiModel.FixedDiscount(
            id = id,
            description = description,
            expirationDate = expirationDate,
            minimumPrice = minimumPrice,
        )

        is FreeShippingCoupon -> CouponUiModel.FreeShipping(
            id = id,
            description = description,
            expirationDate = expirationDate,
            minimumPrice = this.minimumPrice,
        )

        is BuyXGetYCoupon -> CouponUiModel.BuyXGetY(
            id = id,
            description = description,
            expirationDate = expirationDate,
        )

        is PercentageCoupon -> CouponUiModel.Percentage(
            id = id,
            description = description,
            expirationDate = expirationDate,
            startTime = this.startTime,
            endTime = this.endTime,
        )
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as ShoppingApplication
                val route: PurchaseRoute = createSavedStateHandle().toRoute()
                PurchaseViewModel(
                    contentIds = route.contentIds,
                    originalPrice = route.totalPrice,
                    orderRepository = app.orderRepository,
                    couponRepository = app.couponRepository,
                    cartRepository = app.cartRepository,
                    settingRepository = app.settingRepository,
                    paymentNotificationScheduler = app.paymentNotificationScheduler,
                )
            }
        }
    }
}
