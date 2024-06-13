package woowacourse.shopping.view.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.view.cart.model.ShoppingCart
import woowacourse.shopping.view.order.adapter.OnClickCoupon
import woowacourse.shopping.view.order.model.CouponUiModel
import woowacourse.shopping.view.order.model.CouponUiModelMapper.toDomainModel
import woowacourse.shopping.view.order.model.CouponUiModelMapper.toUiModel
import woowacourse.shopping.view.order.state.CouponUiState
import woowacourse.shopping.view.order.state.OrderUiState
import java.time.LocalDate

class OrderViewModel(private val orderRepository: OrderRepository) : ViewModel(), OnClickCoupon {
    private var checkedShoppingCart = ShoppingCart()

    private val _coupons: MutableLiveData<List<CouponUiModel>> = MutableLiveData()
    val coupons: LiveData<List<CouponUiModel>> get() = _coupons

    private val _totalPrice: MutableLiveData<Int> = MutableLiveData(0)
    val totalPrice: LiveData<Int> get() = _totalPrice

    private val _couponDiscount: MutableLiveData<Int> = MutableLiveData(0)
    val couponDiscount: LiveData<Int> get() = _couponDiscount

    private val _deliveryFee: MutableLiveData<Int> = MutableLiveData(SHIPPING_COST)
    val deliveryFee: LiveData<Int> get() = _deliveryFee

    private val _totalPayment: MutableLiveData<Int> = MutableLiveData(0)
    val totalPayment: LiveData<Int> get() = _totalPayment

    private val _couponUiState: MutableLiveData<CouponUiState> = MutableLiveData(CouponUiState.Idle)
    val couponUiState: LiveData<CouponUiState> get() = _couponUiState

    private val _orderUiState: MutableLiveData<OrderUiState> = MutableLiveData(OrderUiState.Idle)
    val orderUiState: LiveData<OrderUiState> get() = _orderUiState

    private var selectedCoupon: CouponUiModel? = null

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            _orderUiState.value = OrderUiState.Failure(throwable.message)
        }

    init {
        loadCoupons()
        calculateTotalPrice()
    }

    private fun loadCoupons() {
        viewModelScope.launch(coroutineExceptionHandler) {
            runCatching {
                orderRepository.getCoupons().getOrThrow()
            }.onSuccess { couponList ->
                _coupons.value =
                    couponList.map { coupon ->
                        coupon.toUiModel()
                    }
            }.onFailure {
                _orderUiState.value = OrderUiState.Failure(it.message)
            }
        }
    }

    fun saveCheckedShoppingCarts(shoppingCart: ShoppingCart) {
        checkedShoppingCart = shoppingCart
        calculateTotalPrice()
    }

    override fun applyCoupon(coupon: CouponUiModel) {
        if (selectedCoupon != null) {
            resetCoupon()
            resetCouponSelection()
        } else {
            val isValidCoupon = applyCouponStrategy(coupon)

            if (isValidCoupon) {
                resetCouponSelection()
                selectedCoupon = coupon
                updateCouponSelection(coupon)
            } else {
                selectedCoupon = null
                resetCouponSelection()
            }

            calculateTotalPayment()
        }
    }

    private fun applyCouponStrategy(coupon: CouponUiModel): Boolean {
        val newTotalPrice =
            coupon.discountStrategy.applyDiscount(
                totalPrice.value!!,
                coupon.toDomainModel(),
                checkedShoppingCart.cartItems.value.orEmpty(),
            )
        return if (totalPrice.value!! >= coupon.minimumAmount &&
            isCouponValid(coupon.expirationDate) &&
            newTotalPrice != totalPrice.value
        ) {
            _couponDiscount.value = totalPrice.value!! - newTotalPrice
            _couponUiState.value = CouponUiState.Applied
            true
        } else {
            val errorMessage =
                if (totalPrice.value!! < coupon.minimumAmount) {
                    ERROR_MESSAGE_MINIMUM_AMOUNT
                } else if (!isCouponValid(coupon.expirationDate)) {
                    ERROR_MESSAGE_EXPIRED_COUPON
                } else {
                    ERROR_MESSAGE_CANNOT_APPLY_COUPON
                }
            _couponUiState.value = CouponUiState.Error(errorMessage)
            false
        }
    }

    private fun updateCouponSelection(coupon: CouponUiModel) {
        _coupons.value =
            _coupons.value?.map {
                it.copy(isSelected = it.id == coupon.id)
            }
    }

    private fun resetCouponSelection() {
        _coupons.value =
            _coupons.value?.map {
                it.copy(isSelected = false)
            }
    }

    private fun resetCoupon() {
        selectedCoupon = null
        _couponDiscount.value = 0
        _deliveryFee.value = SHIPPING_COST
        _couponUiState.value = CouponUiState.Idle
        calculateTotalPayment()
    }

    private fun calculateTotalPrice() {
        _totalPrice.value =
            checkedShoppingCart.cartItems.value.orEmpty().sumOf {
                it.product.cartItemCounter.itemCount * it.product.price
            }
        calculateTotalPayment()
    }

    private fun calculateTotalPayment() {
        _totalPayment.value = _totalPrice.value!! - _couponDiscount.value!! + _deliveryFee.value!!
    }

    private fun isCouponValid(expirationDate: LocalDate): Boolean {
        val now = LocalDate.now()
        return now.isBefore(expirationDate) || now.isEqual(expirationDate)
    }

    fun orderItems() {
        val ids = checkedShoppingCart.cartItems.value?.map { it.id }
        if (ids != null) {
            viewModelScope.launch(coroutineExceptionHandler) {
                runCatching {
                    orderRepository.orderShoppingCart(ids).getOrThrow()
                }.onSuccess {
                    _orderUiState.value = OrderUiState.Success
                }.onFailure { throwable ->
                    _orderUiState.value = OrderUiState.Failure(throwable.message)
                }
            }
        } else {
            _orderUiState.value = OrderUiState.Failure(ERROR_MESSAGE_NO_ITEMS)
        }
    }

    companion object {
        const val SHIPPING_COST = 3000
        private const val ERROR_MESSAGE_EXPIRED_COUPON = "쿠폰이 만료되었습니다."
        private const val ERROR_MESSAGE_MINIMUM_AMOUNT = "최소 주문 금액을 만족하지 못했습니다."
        private const val ERROR_MESSAGE_CANNOT_APPLY_COUPON = "할인 조건을 충족하지 못했습니다."
        private const val ERROR_MESSAGE_NO_ITEMS = "주문할 아이템이 없습니다."
    }
}
