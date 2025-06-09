package woowacourse.shopping.feature.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.carts.repository.CartRepository
import woowacourse.shopping.data.coupons.repository.OrderRepository
import woowacourse.shopping.data.util.mapper.toCartItems
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Coupon

class CouponViewModel(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository
) : ViewModel(
) {

    private val _coupons = MutableLiveData<List<Coupon>>()
    val coupons: LiveData<List<Coupon>> get() = _coupons

    private val _selectedCoupon = MutableLiveData<Coupon?>()
    val selectedCoupon: LiveData<Coupon?> get() = _selectedCoupon

    private val _totalAmount = MutableLiveData<Int>()
    val totalAmount: LiveData<Int> get() = _totalAmount

    private var _originalAmount = MutableLiveData<Int>(0)
    val originalAmount: LiveData<Int> get() = _originalAmount
    private var _cartItems: List<CartItem> = emptyList()
    val cartItems: List<CartItem> get() = _cartItems
    private var _shippingFee = MutableLiveData<Int>(3000)
    val shippingFee: LiveData<Int> get() = _shippingFee

    fun setCartItems(itemIds: List<Int>) {
        viewModelScope.launch {
            val allCartItems = cartRepository.fetchAllCartItems()
            _cartItems = itemIds.map { id ->
                allCartItems.toCartItems().first { it.goods.id == id }
            }
            _originalAmount.value = cartItems.sumOf { it.goods.price * it.quantity }
            updateTotalAmount()
        }

    }

    fun loadCoupons() {
        viewModelScope.launch {
            _coupons.postValue(orderRepository.fetchCoupons())

        }
    }

    fun selectCoupon(coupon: Coupon) {
        _selectedCoupon.value = coupon
        updateTotalAmount()
    }

    private fun updateTotalAmount() {
        val coupon = _selectedCoupon.value
        val discount = coupon?.calculateDiscount(cartItems, _originalAmount.value ?: 0) ?: 0
        _totalAmount.value = (_originalAmount.value ?: 0) - discount
    }

    fun onPayClicked() {
        // 실제 결제 처리 로직은 외부로 위임
    }
}