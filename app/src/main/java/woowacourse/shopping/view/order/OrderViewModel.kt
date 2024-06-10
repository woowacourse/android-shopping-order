package woowacourse.shopping.view.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.view.cart.model.ShoppingCart
import woowacourse.shopping.view.order.adapter.OnClickCoupon
import woowacourse.shopping.view.order.model.CouponUiModel
import woowacourse.shopping.view.order.model.CouponUiModelMapper.toUiModel
import woowacourse.shopping.view.order.state.CouponUiState
import woowacourse.shopping.view.order.state.OrderUiState
import java.time.LocalDate
import java.time.LocalTime

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
                        when (coupon) {
                            is Coupon.FixedDiscountCoupon -> coupon.toUiModel()
                            is Coupon.BogoCoupon -> coupon.toUiModel()
                            is Coupon.FreeShippingCoupon -> coupon.toUiModel()
                            is Coupon.TimeBasedDiscountCoupon -> coupon.toUiModel()
                        }
                    }
            }.onFailure {
                Log.e("OrderViewModel", "loadCoupons: error $it")
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
            val isValidCoupon =
                when (coupon) {
                    is CouponUiModel.FixedDiscountCouponUiModel -> applyFixedDiscountCoupon(coupon)
                    is CouponUiModel.BogoCouponUiModel -> applyBogoCoupon(coupon)
                    is CouponUiModel.FreeShippingCouponUiModel -> applyFreeShippingCoupon(coupon)
                    is CouponUiModel.TimeBasedDiscountCouponUiModel ->
                        applyTimeBasedDiscountCoupon(
                            coupon,
                        )
                }

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

    private fun updateCouponSelection(coupon: CouponUiModel) {
        _coupons.value =
            _coupons.value?.map {
                when (it) {
                    is CouponUiModel.FixedDiscountCouponUiModel -> it.copy(isSelected = it.id == coupon.id)
                    is CouponUiModel.BogoCouponUiModel -> it.copy(isSelected = it.id == coupon.id)
                    is CouponUiModel.FreeShippingCouponUiModel -> it.copy(isSelected = it.id == coupon.id)
                    is CouponUiModel.TimeBasedDiscountCouponUiModel -> it.copy(isSelected = it.id == coupon.id)
                }
            }
    }

    private fun resetCouponSelection() {
        _coupons.value =
            _coupons.value?.map {
                when (it) {
                    is CouponUiModel.FixedDiscountCouponUiModel -> it.copy(isSelected = false)
                    is CouponUiModel.BogoCouponUiModel -> it.copy(isSelected = false)
                    is CouponUiModel.FreeShippingCouponUiModel -> it.copy(isSelected = false)
                    is CouponUiModel.TimeBasedDiscountCouponUiModel -> it.copy(isSelected = false)
                }
            }
    }

    private fun applyFixedDiscountCoupon(coupon: CouponUiModel.FixedDiscountCouponUiModel): Boolean {
        return if (totalPrice.value!! >= coupon.minimumAmount &&
            LocalDate.now()
                .isBefore(coupon.expirationDate)
        ) {
            _couponDiscount.value = coupon.discount
            _couponUiState.value = CouponUiState.Applied
            true
        } else {
            val errorMessage =
                if (totalPrice.value!! < coupon.minimumAmount) {
                    ERROR_MESSAGE_MINIMUM_AMOUNT
                } else {
                    ERROR_MESSAGE_EXPIRED_COUPON
                }
            _couponUiState.value =
                CouponUiState.Error(errorMessage)
            false
        }
    }

    private fun applyBogoCoupon(coupon: CouponUiModel.BogoCouponUiModel): Boolean {
        val cartItems = checkedShoppingCart.cartItems.value.orEmpty()
        val bogoItems =
            cartItems.filter { it.product.cartItemCounter.itemCount >= coupon.buyQuantity }
        val mostExpensiveItem = bogoItems.maxByOrNull { it.product.price }

        return if (mostExpensiveItem != null && LocalDate.now().isBefore(coupon.expirationDate)) {
            val discountAmount = mostExpensiveItem.product.price
            _couponDiscount.value = discountAmount
            _couponUiState.value = CouponUiState.Applied
            true
        } else {
            val errorMessage =
                if (mostExpensiveItem == null) {
                    ERROR_MESSAGE_NOT_BOGO_ITEM
                } else {
                    ERROR_MESSAGE_EXPIRED_COUPON
                }
            _couponUiState.value =
                CouponUiState.Error(errorMessage)
            false
        }
    }

    private fun applyFreeShippingCoupon(coupon: CouponUiModel.FreeShippingCouponUiModel): Boolean {
        return if (totalPrice.value!! >= coupon.minimumAmount &&
            LocalDate.now()
                .isBefore(coupon.expirationDate)
        ) {
            _deliveryFee.value = 0
            _couponUiState.value = CouponUiState.Applied
            true
        } else {
            val errorMessage =
                if (totalPrice.value!! < coupon.minimumAmount) {
                    ERROR_MESSAGE_MINIMUM_AMOUNT
                } else {
                    ERROR_MESSAGE_EXPIRED_COUPON
                }
            _couponUiState.value =
                CouponUiState.Error(
                    errorMessage,
                )
            false
        }
    }

    private fun applyTimeBasedDiscountCoupon(coupon: CouponUiModel.TimeBasedDiscountCouponUiModel): Boolean {
        val now = LocalTime.now()
        return if (now.isAfter(coupon.availableTimeStart) && now.isBefore(coupon.availableTimeEnd) &&
            LocalDate.now()
                .isBefore(coupon.expirationDate)
        ) {
            _couponDiscount.value = (_totalPrice.value!! * coupon.discount / 100)
            _couponUiState.value = CouponUiState.Applied
            true
        } else {
            val errorMessage =
                if (now.isBefore(coupon.availableTimeStart) || now.isAfter(coupon.availableTimeEnd)) {
                    ERROR_MESSAGE_NOT_AVAILABLE_TIME
                } else {
                    ERROR_MESSAGE_EXPIRED_COUPON
                }
            _couponUiState.value =
                CouponUiState.Error(errorMessage)
            false
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

    fun orderItems() {
        // 주문 로직 구현
    }

    companion object {
        const val SHIPPING_COST = 3000
        private const val ERROR_MESSAGE_EXPIRED_COUPON = "쿠폰이 만료되었습니다."
        private const val ERROR_MESSAGE_MINIMUM_AMOUNT = "최소 주문 금액을 만족하지 못했습니다."
        private const val ERROR_MESSAGE_NOT_BOGO_ITEM = "적용할 수 없는 쿠폰입니다.(BoGo 조건 미충족)"
        private const val ERROR_MESSAGE_NOT_AVAILABLE_TIME = "쿠폰이 적용 가능한 시간이 아닙니다."
        private const val ERROR_MESSAGE_NO_ITEMS = "주문할 아이템이 없습니다."
    }
}
