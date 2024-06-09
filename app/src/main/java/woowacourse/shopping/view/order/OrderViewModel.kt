package woowacourse.shopping.view.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.view.cart.model.ShoppingCart
import woowacourse.shopping.view.order.adapter.OnClickCoupon
import java.time.LocalDate
import java.time.LocalTime

class OrderViewModel(private val orderRepository: OrderRepository) : ViewModel(), OnClickCoupon {
    private var checkedShoppingCart = ShoppingCart()

    private val _coupons: MutableLiveData<List<Coupon>> = MutableLiveData()
    val coupons: LiveData<List<Coupon>> get() = _coupons

    private val _totalPrice: MutableLiveData<Int> = MutableLiveData(0)
    val totalPrice: LiveData<Int> get() = _totalPrice

    private val _couponDiscount: MutableLiveData<Int> = MutableLiveData(0)
    val couponDiscount: LiveData<Int> get() = _couponDiscount

    private val _deliveryFee: MutableLiveData<Int> = MutableLiveData(SHIPPING_COST)
    val deliveryFee: LiveData<Int> get() = _deliveryFee

    private val _totalPayment: MutableLiveData<Int> = MutableLiveData(0)
    val totalPayment: LiveData<Int> get() = _totalPayment

    private val _couponUiState: MutableLiveData<CouponUiState> = MutableLiveData(CouponUiState())
    val couponUiState: LiveData<CouponUiState> get() = _couponUiState

    private var selectedCoupon: Coupon? = null

    init {
        loadCoupons()
        calculateTotalPrice()
    }

    private fun loadCoupons() {
        viewModelScope.launch {
            runCatching {
                orderRepository.getCoupons().getOrThrow()
            }.onSuccess { couponList ->
                _coupons.value = couponList
            }.onFailure {
                Log.e("OrderViewModel", "loadCoupons: error $it")
            }
        }
    }

    fun saveCheckedShoppingCarts(shoppingCart: ShoppingCart) {
        checkedShoppingCart = shoppingCart
        calculateTotalPrice()
    }

    override fun applyCoupon(coupon: Coupon) {
        if (selectedCoupon == coupon) {
            resetCoupon()
        } else {
            selectedCoupon = coupon
            when (coupon) {
                is Coupon.FixedDiscountCoupon -> applyFixedDiscountCoupon(coupon)
                is Coupon.BogoCoupon -> applyBogoCoupon(coupon)
                is Coupon.FreeShippingCoupon -> applyFreeShippingCoupon(coupon)
                is Coupon.TimeBasedDiscountCoupon -> applyTimeBasedDiscountCoupon(coupon)
            }
            calculateTotalPayment()
        }
    }

    private fun applyFixedDiscountCoupon(coupon: Coupon.FixedDiscountCoupon) {
        if (totalPrice.value!! >= coupon.minimumAmount &&
            LocalDate.now().isBefore(coupon.expirationDate)
        ) {
            _couponDiscount.value = coupon.discount
            _couponUiState.value = CouponUiState(isCouponApplied = true)
        } else if (totalPrice.value!! >= coupon.minimumAmount) {
            _couponDiscount.value = 0
            _couponUiState.value = CouponUiState(isCouponApplied = false, errorMessage = "쿠폰이 만료되었습니다.")
        } else {
            _couponDiscount.value = 0
            _couponUiState.value = CouponUiState(isCouponApplied = false, errorMessage = "최소 주문 금액을 만족하지 못했습니다.")
        }
    }

    private fun applyBogoCoupon(coupon: Coupon.BogoCoupon) {
        val cartItems = checkedShoppingCart.cartItems.value.orEmpty()
        val bogoItems =
            cartItems.filter { it.product.cartItemCounter.itemCount >= coupon.buyQuantity }
        val mostExpensiveItem = bogoItems.maxByOrNull { it.product.price }

        if (mostExpensiveItem != null && LocalDate.now().isBefore(coupon.expirationDate)) {
            val discountAmount = mostExpensiveItem.product.price
            _couponDiscount.value = discountAmount
            _couponUiState.value = CouponUiState(isCouponApplied = true)
        } else if (mostExpensiveItem == null) {
            _couponDiscount.value = 0
            _couponUiState.value = CouponUiState(isCouponApplied = false, errorMessage = "적용할 수 없는 쿠폰입니다.(BoGo 조건 미충족)")
        } else {
            _couponDiscount.value = 0
            _couponUiState.value = CouponUiState(isCouponApplied = false, errorMessage = "쿠폰이 만료되었습니다.")
        }
    }

    private fun applyFreeShippingCoupon(coupon: Coupon.FreeShippingCoupon) {
        if (totalPrice.value!! >= coupon.minimumAmount &&
            LocalDate.now().isBefore(coupon.expirationDate)
        ) {
            _deliveryFee.value = 0
            _couponUiState.value = CouponUiState(isCouponApplied = true)
        } else if (totalPrice.value!! >= coupon.minimumAmount) {
            _deliveryFee.value = SHIPPING_COST
            _couponUiState.value = CouponUiState(isCouponApplied = false, errorMessage = "쿠폰이 만료되었습니다.")
        } else {
            _deliveryFee.value = SHIPPING_COST
            _couponUiState.value = CouponUiState(isCouponApplied = false, errorMessage = "최소 주문 금액을 만족하지 못했습니다.")
        }
    }

    private fun applyTimeBasedDiscountCoupon(coupon: Coupon.TimeBasedDiscountCoupon) {
        val now = LocalTime.now()
        if (now.isAfter(coupon.availableTimeStart) &&
            now.isBefore(coupon.availableTimeEnd) &&
            LocalDate.now().isBefore(coupon.expirationDate)
        ) {
            _couponDiscount.value = (_totalPrice.value!! * coupon.discount / 100)
            _couponUiState.value = CouponUiState(isCouponApplied = true)
        } else if (now.isBefore(coupon.availableTimeStart) || now.isAfter(coupon.availableTimeEnd)) {
            _couponDiscount.value = 0
            _couponUiState.value = CouponUiState(isCouponApplied = false, errorMessage = "쿠폰이 적용 가능한 시간이 아닙니다.")
        } else {
            _couponDiscount.value = 0
            _couponUiState.value = CouponUiState(isCouponApplied = false, errorMessage = "쿠폰이 만료되었습니다.")
        }
    }

    private fun resetCoupon() {
        selectedCoupon = null
        _couponDiscount.value = 0
        _deliveryFee.value = SHIPPING_COST
        _couponUiState.value = CouponUiState(isCouponApplied = false)
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
        _totalPayment.value =
            _totalPrice.value!! - _couponDiscount.value!! + _deliveryFee.value!!
    }

    fun orderItems() {
        // 주문 로직 구현
    }

    companion object {
        const val SHIPPING_COST = 3000
    }
}
