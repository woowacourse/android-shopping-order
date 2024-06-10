package woowacourse.shopping.presentation.ui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.remote.dto.request.OrderRequest
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.Repository
import woowacourse.shopping.domain.coupon.Coupon
import woowacourse.shopping.presentation.ui.EventState
import woowacourse.shopping.presentation.ui.UiState

class PaymentViewModel(
    private val repository: Repository,
    private val ids: List<Long>,
) : ViewModel(), CouponActionHandler {
    private val _coupons = MutableLiveData<UiState<List<Coupon>>>(UiState.Loading)
    val coupons: LiveData<UiState<List<Coupon>>> = _coupons

    private val _errorHandler = MutableLiveData<EventState<String>>()
    val errorHandler: LiveData<EventState<String>> get() = _errorHandler

    private val _orderProducts = MutableLiveData<UiState<List<CartProduct>>>(UiState.Loading)
    val orderProducts: LiveData<UiState<List<CartProduct>>> get() = _orderProducts

    private val _eventHandler = MutableLiveData<EventState<CouponEvent>>()
    val eventHandler: LiveData<EventState<CouponEvent>> get() = _eventHandler

    private val _couponPrice = MutableLiveData<Int>(0)
    val couponPrice: LiveData<Int> get() = _couponPrice

    private val _deliveryPrice = MutableLiveData<Int>(3000)
    val deliveryPrice: LiveData<Int> get() = _deliveryPrice

    private val _totalPrice = MediatorLiveData<Int>()
    val totalPrice: LiveData<Int> get() = _totalPrice

    init {
        viewModelScope.launch {
            orderProducts()
        }

        _totalPrice.addSource(_orderProducts) { calculateTotalPrice() }
        _totalPrice.addSource(_couponPrice) { calculateTotalPrice() }
        _totalPrice.addSource(_deliveryPrice) { calculateTotalPrice() }
    }

    fun getCoupons() = viewModelScope.launch {
        repository.getCoupons()
            .onSuccess {
                _coupons.value = UiState.Success(it)
            }.onFailure {
                _errorHandler.value = EventState(COUPON_LOAD_ERROR)
            }
    }

    private suspend fun orderProducts() {
        repository.getCartItems(0, 1000)
            .onSuccess {
                if (it == null) {
                    _errorHandler.value = EventState(COUPON_LOAD_ERROR)
                } else {
                    val filteredCartItems =
                        it.filter { cartProduct -> ids.contains(cartProduct.cartId) }
                    _orderProducts.value = UiState.Success(filteredCartItems)
                }
            }
            .onFailure {
                _errorHandler.value = EventState(COUPON_LOAD_ERROR)
            }
    }

    private fun calculateTotalPrice() {
        if (_orderProducts.value is UiState.Success) {
            val orderTotal = (_orderProducts.value as UiState.Success).data.sumOf {
                it.quantity * it.price.toInt()
            }
            _totalPrice.value = orderTotal - (_couponPrice.value ?: 0) + (_deliveryPrice.value ?: 0)
        }
    }

    override fun order() {
        viewModelScope.launch {
            if (_orderProducts.value is UiState.Success) {
                repository.submitOrders(
                    OrderRequest(
                        ids.map { it.toInt() },
                    )
                ).onSuccess {
                    _eventHandler.value = EventState(CouponEvent.SuccessPay)
                }.onFailure { _errorHandler.value = EventState(PAYMENT_ERROR) }
            } else {
                _errorHandler.value = EventState(PAYMENT_ERROR)
            }
        }
    }

    override fun onCouponClick(selectedCoupon: Coupon) {
        val coupons = (_coupons.value as? UiState.Success)?.data ?: return

        coupons.find { it.id == selectedCoupon.id }?.apply {
            isSelected = !isSelected
            val discountPrice = discountPrice(
                totalPrice.value ?: 0,
                (_orderProducts.value as UiState.Success).data
            )
            if (isSelected && discountPrice == 0) {
                _errorHandler.value = EventState(APPLY_COUPON_ERROR)
                isSelected = false
            }
            _couponPrice.value = if (isSelected) discountPrice else 0
        }

        coupons.forEach { if (it.id != selectedCoupon.id) it.isSelected = false }
        _coupons.value = UiState.Success(coupons)
    }

    companion object {
        const val COUPON_LOAD_ERROR = "LOAD ERROR"
        const val PAYMENT_ERROR = "PAYMENT ERROR"
        const val APPLY_COUPON_ERROR = "APPLY COUPON ERROR"
    }
}
