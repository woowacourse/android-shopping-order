package woowacourse.shopping.feature.payment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.AlarmReceiver
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.constants.MockData
import woowacourse.shopping.data.repository.cart.CartRepository
import woowacourse.shopping.data.repository.coupon.CouponRepository
import woowacourse.shopping.data.repository.order.OrderRepository
import woowacourse.shopping.data.repository.product.ProductRepository
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.coupon.BuyXGetYCoupon
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.domain.coupon.FixedDiscountCoupon
import woowacourse.shopping.domain.coupon.FreeShippingCoupon
import woowacourse.shopping.domain.coupon.PercentageDiscountCoupon
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class PaymentViewModel(
    private val application: ShoppingApplication,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private val _couponEvent = MutableSharedFlow<CouponEvent>()
    val couponEvent: SharedFlow<CouponEvent> = _couponEvent.asSharedFlow()

    private val _paymentEvent = MutableSharedFlow<PaymentEvent>()
    val paymentEvent: SharedFlow<PaymentEvent> = _paymentEvent.asSharedFlow()

    lateinit var productRepository: ProductRepository
    lateinit var cartRepository: CartRepository
    lateinit var orderRepository: OrderRepository
    lateinit var couponRepository: CouponRepository
    lateinit var couponList: List<Coupon>

    private val sharedPref = application.getSharedPreferences("setting", Context.MODE_PRIVATE)

    init {
        viewModelScope.launch {
            val appDependencies = application.appDependenciesDeferred.await()
            productRepository = appDependencies.productRepository
            cartRepository = appDependencies.cartRepository
            orderRepository = appDependencies.orderRepository
            couponRepository = appDependencies.couponRepository
            couponList = couponRepository.loadCoupons()
        }
    }

    fun startPaymentAlarm() {
        if (!sharedPref.getBoolean("notification", true)) return
        val intent = Intent(application, AlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            application, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerTime = System.currentTimeMillis() + (5 * 60 * 1000)
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }

    fun cancelPaymentAlarm() {
        val intent = Intent(application, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(application, 0, intent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        pendingIntent?.let {
            val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(it)
        }
    }

    fun loadCart(cartContentIds: List<Long>) {
        viewModelScope.launch {
            val cart = cartRepository.loadCart()
            val targetContents = cart.cartContents.filter { it.id in cartContentIds }
            val totalPrice = targetContents.sumOf { it.quantity * it.product.priceAmount() }
            _uiState.update {
                it.copy(
                    totalPrice = totalPrice,
                    totalPaymentPrice = totalPrice + it.shippingFee
                )
            }
            loadCoupon()
        }
    }

    fun loadCoupon() {
        val currentTotalPrice = _uiState.value.totalPrice

        viewModelScope.launch {
            couponList = couponRepository.loadCoupons()
            val couponChooseList = couponList.filter {
                val isDiscountable = couponValid(it, cartRepository.loadCart(), currentTotalPrice)
                isDiscountable && !it.isExpired(LocalDate.now())
            }

            val couponCheckMap = couponChooseList.associate { it.code to false }
            _uiState.value = _uiState.value.copy(
                couponCheckMap = couponCheckMap,
                couponList = couponChooseList.map {
                    CouponUiModel(
                        code = it.code,
                        title = it.description,
                        year = it.expirationDate.year,
                        month = it.expirationDate.monthValue,
                        day = it.expirationDate.dayOfMonth,
                        minimumPrice = when (it) {
                            is FixedDiscountCoupon -> {
                                it.minimumAmount
                            }

                            is FreeShippingCoupon -> {
                                it.minimumAmount
                            }

                            is BuyXGetYCoupon -> {
                                0
                            }

                            is PercentageDiscountCoupon -> {
                                0
                            }
                        }
                    )
                },
                shippingFee = 3000
            )
        }
    }

    fun couponCheck(code: String) {
        viewModelScope.launch {
            val coupon = couponList.find { it.code == code } ?: return@launch

            val currentMap = _uiState.value.couponCheckMap.toMutableMap()
            val isCurrentlyChecked = currentMap[code] ?: false

            if (!isCurrentlyChecked) {
                val isValid = couponValid(coupon, cartRepository.loadCart(), uiState.value.totalPrice)

                if (isValid) {
                    _couponEvent.emit(CouponEvent.Success("쿠폰 적용이 되었습니다."))
                    currentMap.keys.forEach { currentMap[it] = false }
                    currentMap[code] = true
                } else {
                    _couponEvent.emit(CouponEvent.Failed("쿠폰 적용이 불가능합니다."))
                }
            } else {
                currentMap[code] = false
            }

            var newShippingFee = 3000
            var newCouponDiscountPrice = 0

            val activeCouponCode = currentMap.entries.find { it.value }?.key
            if (activeCouponCode != null) {
                val activeCoupon = couponList.find { it.code == activeCouponCode }
                when (activeCoupon) {
                    is FixedDiscountCoupon -> {
                        newCouponDiscountPrice = activeCoupon.discount
                    }

                    is FreeShippingCoupon -> {
                        newShippingFee = 0
                    }

                    is BuyXGetYCoupon -> {
                        val cart = cartRepository.loadCart()
                        val maxQuantityProduct = cart.cartContents.maxByOrNull { it.quantity }?.product
                        newCouponDiscountPrice = activeCoupon.calculateDiscountPrice(
                            maxQuantityProduct?.priceAmount() ?: 0
                        )
                    }

                    is PercentageDiscountCoupon -> {
                        newCouponDiscountPrice =
                            activeCoupon.calculateDiscountPrice(uiState.value.totalPrice)
                    }

                    else -> {}
                }
            }

            _uiState.update {
                it.copy(
                    couponCheckMap = currentMap,
                    shippingFee = newShippingFee,
                    couponDiscountPrice = newCouponDiscountPrice,
                    totalPaymentPrice = it.totalPrice + newShippingFee - newCouponDiscountPrice
                )
            }
        }
    }

    fun couponValid(coupon: Coupon, cart: Cart, currentTotalPrice: Int): Boolean {
        return when (coupon) {
            is FixedDiscountCoupon -> {
                coupon.isDiscountable(currentTotalPrice)
            }

            is FreeShippingCoupon -> {
                coupon.isDiscountable(currentTotalPrice)
            }

            is BuyXGetYCoupon -> {
                val maxQuantityContent = cart.cartContents.maxByOrNull { it.quantity }
                val maxQuantity = maxQuantityContent?.quantity ?: 0
                coupon.isDiscountable(maxQuantity) && maxQuantity >= coupon.getQuantity + coupon.buyQuantity
            }

            is PercentageDiscountCoupon -> {
                coupon.isDiscountingTime(LocalDateTime.now().toLocalTime())
            }
        }
    }

    fun order(cartContentIds: List<Long>) {
        viewModelScope.launch {
            try {
                orderRepository.orders(cartContentIds)
                cartContentIds.forEach {
                    cartRepository.remove(it)
                }
                cancelPaymentAlarm()
                _paymentEvent.emit(PaymentEvent.Success("주문이 완료되었습니다."))
            } catch (e: Exception) {
                _paymentEvent.emit(PaymentEvent.Failed("주문에 실패하였습니다. ${e.message}"))
            }
        }
    }

    companion object {
        val Factory =
            viewModelFactory {
                initializer {
                    val app = this[APPLICATION_KEY] as ShoppingApplication
                    PaymentViewModel(app)
                }
            }
    }
}


data class PaymentUiState(
    val couponCheckMap: Map<String, Boolean> = emptyMap(),
    val couponList: List<CouponUiModel> = emptyList(),
    val totalPrice: Int = 0,
    val couponDiscountPrice: Int = 0,
    val shippingFee: Int = 0,
    val totalPaymentPrice: Int = 0
)

data class CouponUiModel(
    val code: String,
    val title: String,
    val year: Int,
    val month: Int,
    val day: Int,
    val minimumPrice: Int,
)

sealed interface CouponEvent {
    data class Success(val message: String) : CouponEvent
    data class Failed(val message: String) : CouponEvent
}

sealed interface PaymentEvent {
    data class Success(val message: String) : PaymentEvent
    data class Failed(val message: String) : PaymentEvent
}
