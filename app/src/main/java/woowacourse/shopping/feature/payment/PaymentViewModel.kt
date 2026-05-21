package woowacourse.shopping.feature.payment

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
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.constants.MockData
import woowacourse.shopping.data.repository.cart.CartRepository
import woowacourse.shopping.data.repository.order.OrderRepository
import woowacourse.shopping.data.repository.product.ProductRepository
import woowacourse.shopping.domain.CouponCode

class PaymentViewModel(
    private val application: ShoppingApplication,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private val _couponEvent = MutableSharedFlow<CouponEvent>()
    val couponEvent: SharedFlow<CouponEvent> = _couponEvent.asSharedFlow()

    private val _paymentEvent = MutableSharedFlow<PaymentEvent>()
    val paymentEvent: SharedFlow<PaymentEvent> = _paymentEvent.asSharedFlow()

    private val couponList = MockData.MOCK_COUPONS

    lateinit var productRepository: ProductRepository
    lateinit var cartRepository: CartRepository
    lateinit var orderRepository: OrderRepository

    init {
        viewModelScope.launch {
            val appDependencies = application.appDependenciesDeferred.await()
            productRepository = appDependencies.productRepository
            cartRepository = appDependencies.cartRepository
            orderRepository = appDependencies.orderRepository

            val couponCheckMap = couponList.associate { CouponCode.toCodeString(it.code) to false }
            _uiState.value = _uiState.value.copy(
                couponCheckMap = couponCheckMap,
                couponList = couponList.map {
                    CouponUiModel(
                        code = CouponCode.toCodeString(it.code),
                        title = it.title,
                        year = it.expiryDate.year,
                        month = it.expiryDate.monthValue,
                        day = it.expiryDate.dayOfMonth,
                        minimumPrice = it.minimumPrice
                    )
                },
                shippingFee = 3000
            )
        }
    }

    fun loadCart(cartContentIds: List<Long>) {
        viewModelScope.launch {
            val cart = cartRepository.loadCart()
            val targetContents = cart.cartContents.filter{ it.id in cartContentIds }
            val totalPrice = targetContents.sumOf { it.quantity * it.product.priceAmount() }
            _uiState.update {
                it.copy(
                    totalPrice = totalPrice,
                    totalPaymentPrice = totalPrice + it.shippingFee
                )
            }
        }
    }

    fun couponCheck(code: String) {
        viewModelScope.launch {
            val cart = cartRepository.loadCart()
            val twoMoreCartItems = cart.cartContents.filter {
                it.quantity > 2
            }

            val newCouponCheckMap = when (CouponCode.fromCodeString(code)) {
                CouponCode.FIXED5000 -> {
                    if (cart.cartContents.sumOf { it.quantity * it.product.priceAmount() } >= 100000) {
                        _couponEvent.emit(CouponEvent.Success("쿠폰 적용이 되었습니다."))
                        _uiState.value.couponCheckMap.toMutableMap().apply {
                            this[code] = this[code]?.not() ?: false
                        }.toMap()
                    } else {
                        _couponEvent.emit(CouponEvent.Failed("주문 금액이 10만원 이상이 아닙니다."))
                        _uiState.value.couponCheckMap
                    }
                }

                CouponCode.BOGO -> {
                    if (twoMoreCartItems.isNotEmpty()) {
                        _couponEvent.emit(CouponEvent.Success("쿠폰 적용이 되었습니다."))
                        _uiState.value.couponCheckMap.toMutableMap().apply {
                            this[code] = this[code]?.not() ?: false
                        }.toMap()
                    } else {
                        _couponEvent.emit(CouponEvent.Failed("2개 이상 구매한 상품이 존재하지 않습니다."))
                        _uiState.value.couponCheckMap
                    }
                }

                CouponCode.FREESHIPPING -> {
                    if (cart.cartContents.sumOf { it.quantity * it.product.priceAmount() } >= 50000) {
                        _couponEvent.emit(CouponEvent.Success("쿠폰 적용이 되었습니다."))
                        _uiState.value.couponCheckMap.toMutableMap().apply {
                            this[code] = this[code]?.not() ?: false
                        }.toMap()
                    } else {
                        _couponEvent.emit(CouponEvent.Failed("주문 금액이 5만원 이상이 아닙니다."))
                        _uiState.value.couponCheckMap
                    }
                }

                CouponCode.MIRACLESALE -> {
                    _uiState.value.couponCheckMap.toMutableMap().apply {
                        this[code] = this[code]?.not() ?: false
                    }.toMap()
                }
            }

            val shippingFee = if (newCouponCheckMap[CouponCode.toCodeString(CouponCode.FREESHIPPING)] == true) 0 else 3000
            val couponDiscountPrice = if (newCouponCheckMap[CouponCode.toCodeString(CouponCode.FIXED5000)] == true) 5000 else 0
            val couponRateDiscountPrice = if (newCouponCheckMap[CouponCode.toCodeString(CouponCode.MIRACLESALE)] == true) (_uiState.value.totalPrice * 0.30).toInt() else 0
            val couponTwoMoreDiscountPrice = if (newCouponCheckMap[CouponCode.toCodeString(CouponCode.BOGO)] == true) twoMoreCartItems.maxOf{ it.product.priceAmount() } else 0

            _uiState.update {
                it.copy(
                    couponCheckMap = newCouponCheckMap,
                    shippingFee = shippingFee,
                    couponDiscountPrice = couponDiscountPrice + couponRateDiscountPrice + couponTwoMoreDiscountPrice,
                    totalPaymentPrice = it.totalPrice + shippingFee - couponDiscountPrice - couponRateDiscountPrice - couponTwoMoreDiscountPrice
                )
            }
        }
    }

    fun order(cartContentIds: List<Long>) {
        viewModelScope.launch {
            try {
                orderRepository.orders(cartContentIds)
                _paymentEvent.emit(PaymentEvent.Success("주문이 완료되었습니다."))
            } catch (e: Exception) {
                _paymentEvent.emit(PaymentEvent.Failed("주문에 실패하였습니다."))
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
