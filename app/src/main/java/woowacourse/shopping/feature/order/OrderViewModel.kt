package woowacourse.shopping.feature.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.carts.repository.CartRepository
import woowacourse.shopping.data.coupons.repository.OrderRepository
import woowacourse.shopping.data.util.mapper.toCartItems
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.util.MutableSingleLiveData
import woowacourse.shopping.util.SingleLiveData

sealed class OrderUiEvent {
    data class ShowToast(val messageKey: ToastMessageKey) : OrderUiEvent()
    data object OrderSuccess : OrderUiEvent()
}

enum class ToastMessageKey {
    FAIL_ORDER,
    FAIL_LOAD_COUPON
}

class OrderViewModel(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

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

    private var _uiEvent = MutableSingleLiveData<OrderUiEvent>()
    val uiEvent: SingleLiveData<OrderUiEvent> get() = _uiEvent

    private val _discountAmount = MediatorLiveData<Int>().apply {
        addSource(_selectedCoupon) { updateDiscountAmount() }
        addSource(_originalAmount) { updateDiscountAmount() }
        addSource(_shippingFee) { updateDiscountAmount() }
    }
    val discountAmount: LiveData<Int> get() = _discountAmount

    private fun updateDiscountAmount() {
        val discount = _selectedCoupon.value?.calculateDiscount(
            _cartItems,
            _originalAmount.value ?: 0,
            _shippingFee.value ?: 3000
        ) ?: 0
        _discountAmount.value = discount
    }

    fun setCartItems(itemIds: List<Int>) {
        viewModelScope.launch {
            val allCartItems = cartRepository.fetchAllCartItems()
            _cartItems = itemIds.map { id ->
                allCartItems.toCartItems().first { it.goods.id == id }
            }
            val origin = _cartItems.sumOf { it.goods.price * it.quantity }
            val shipping = _shippingFee.value ?: 0
            _originalAmount.value = origin + shipping
            updateTotalAmount()
        }
    }

    fun loadCoupons() {
        viewModelScope.launch {
            try {
                _coupons.value = orderRepository.fetchCoupons()
            } catch (e: Exception) {
                _uiEvent.setValue(OrderUiEvent.ShowToast(ToastMessageKey.FAIL_LOAD_COUPON))
            }
        }
    }

    fun selectCoupon(coupon: Coupon) {
        _selectedCoupon.value = if (_selectedCoupon.value == coupon) null else coupon
        updateTotalAmount()
    }

    private fun updateTotalAmount() {
        val coupon = _selectedCoupon.value
        val discount = coupon?.calculateDiscount(cartItems, _originalAmount.value ?: 0) ?: 0
        _totalAmount.value = (_originalAmount.value ?: 0) - discount
    }

    fun onPayClicked() {
        viewModelScope.launch {
            try {
                orderRepository.addOrder(_cartItems.map { it.id })
                _uiEvent.setValue(OrderUiEvent.OrderSuccess)
            } catch (e: Exception) {
                _uiEvent.setValue(OrderUiEvent.ShowToast(ToastMessageKey.FAIL_ORDER))
            }
        }
    }
}
