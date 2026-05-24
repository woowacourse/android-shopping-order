package woowacourse.shopping.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import woowacourse.shopping.model.coupon.Coupon
import woowacourse.shopping.model.order.FixedShippingPolicy
import woowacourse.shopping.model.order.OrderItem
import woowacourse.shopping.model.order.ShippingPolicy
import woowacourse.shopping.model.product.Money
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.repository.CouponRepository
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.ShoppingRepositoryProvider
import woowacourse.shopping.ui.payment.uistate.CouponUiModelMapper
import woowacourse.shopping.ui.payment.uistate.PaymentUiState
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

class PaymentViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val couponRepository: CouponRepository,
    private val shippingPolicy: ShippingPolicy,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private var orderItems: List<OrderItem> = emptyList()
    private var cartItemIds: List<Long> = emptyList()
    private var coupons: List<Coupon> = emptyList()

    init {
        loadOrderItems()
        loadCoupons()
    }

    private fun loadOrderItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val cartCountResult = cartRepository.count()
            val totalCount = cartCountResult.getOrDefault(0)

            cartRepository
                .getCartPage(0, totalCount.coerceAtLeast(1))
                .onSuccess { cartPage ->
                    cartItemIds = cartPage.items.map { it.cartItemId }
                    val productIds = cartPage.items.map { it.productId }.toSet()

                    productRepository
                        .findAllByIds(productIds)
                        .onSuccess { productsMap ->
                            orderItems =
                                cartPage.items.mapNotNull { cartItem ->
                                    val product = productsMap[cartItem.productId] ?: return@mapNotNull null
                                    OrderItem(
                                        productId = cartItem.productId,
                                        price = product.price,
                                        quantity = cartItem.quantity,
                                    )
                                }
                            _uiState.update { it.copy(isLoading = false) }
                            calculateAmounts()
                        }.onFailure { throwable ->
                            _uiState.update { it.copy(isLoading = false, errorMessage = throwable.message) }
                        }
                }.onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = throwable.message) }
                }
        }
    }

    fun pay() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            cartRepository
                .createOrder(cartItemIds)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isOrderCompleted = true) }
                }.onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = throwable.message) }
                }
        }
    }

    private fun calculateAmounts() {
        val totalProductPrice =
            orderItems.fold(Money.ZERO) { acc, item ->
                acc + (item.price * item.quantity)
            }
        val baseShippingFee = shippingPolicy.calculateShippingFee(totalProductPrice)

        val selectedCouponUiModel = _uiState.value.coupons.find { it.isSelected }
        val selectedCoupon = coupons.find { it.id == selectedCouponUiModel?.id }

        val discountAmount =
            selectedCoupon?.discountPolicy?.calculateDiscount(
                items = orderItems,
                totalProductAmount = totalProductPrice,
                shippingFee = baseShippingFee,
            ) ?: Money.ZERO

        val finalShippingFee =
            if (selectedCoupon is Coupon.FreeShipping && discountAmount > Money.ZERO) {
                Money.ZERO
            } else {
                baseShippingFee
            }

        val actualProductDiscount = if (selectedCoupon is Coupon.FreeShipping) Money.ZERO else discountAmount
        val finalProductPrice = (totalProductPrice - actualProductDiscount).coerceAtLeast(Money.ZERO)

        val totalPaymentAmount = finalProductPrice + finalShippingFee

        val formattedDiscount = formatMoney(actualProductDiscount)
        val couponDiscountDisplay = if (actualProductDiscount > Money.ZERO) "-$formattedDiscount" else formattedDiscount

        _uiState.update {
            it.copy(
                orderAmount = formatMoney(totalProductPrice),
                couponDiscountAmount = couponDiscountDisplay,
                deliveryFee = formatMoney(finalShippingFee),
                totalPaymentAmount = formatMoney(totalPaymentAmount),
            )
        }
    }

    private fun Money.coerceAtLeast(minimumValue: Money): Money = if (this < minimumValue) minimumValue else this

    private fun formatMoney(money: Money): String = NumberFormat.getInstance(Locale.KOREA).format(money.value) + "원"

    private fun loadCoupons() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            couponRepository
                .getCoupons()
                .onSuccess { fetchedCoupons ->
                    val currentDate = LocalDate.now()
                    val currentTime = LocalTime.now()
                    val applicableCoupons = fetchedCoupons.filter { it.isApplicable(currentDate, currentTime) }
                    coupons = applicableCoupons

                    val couponUiModels = applicableCoupons.map { CouponUiModelMapper.toUiModel(it) }
                    _uiState.update {
                        it.copy(
                            coupons = couponUiModels,
                            isLoading = false,
                        )
                    }
                    calculateAmounts()
                }.onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = throwable.message) }
                }
        }
    }

    fun selectCoupon(couponId: Long) {
        _uiState.update { currentState ->
            val updatedCoupons =
                currentState.coupons.map { uiModel ->
                    if (uiModel.id == couponId) {
                        uiModel.copy(isSelected = !uiModel.isSelected)
                    } else {
                        uiModel.copy(isSelected = false)
                    }
                }
            currentState.copy(coupons = updatedCoupons)
        }
        calculateAmounts()
    }
}

class PaymentViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        PaymentViewModel(
            cartRepository = ShoppingRepositoryProvider.cartRepository,
            productRepository = ShoppingRepositoryProvider.productRepository,
            couponRepository = ShoppingRepositoryProvider.couponRepository,
            shippingPolicy = FixedShippingPolicy(),
        ) as T
}
