package woowacourse.shopping.presentation.ui.payment

import android.util.Log
import androidx.lifecycle.LiveData
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

    init {
        viewModelScope.launch {
            orderProducts()
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


    fun getCoupons() = viewModelScope.launch {
        repository.getCoupons()
            .onSuccess {
                _coupons.value = UiState.Success(it)
            }.onFailure {
                _errorHandler.value = EventState(COUPON_LOAD_ERROR)
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
                // Handle the case when _cartProducts.value is not UiState.Success
            }
        }
    }

    companion object {
        const val COUPON_LOAD_ERROR = "LOAD ERROR"
        const val PAYMENT_ERROR = "PAYMENT ERROR"
    }
}
