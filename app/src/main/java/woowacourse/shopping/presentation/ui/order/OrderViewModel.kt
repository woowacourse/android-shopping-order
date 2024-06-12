package woowacourse.shopping.presentation.ui.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.data.database.OrderDatabase
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.coupon.CouponState
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository

class OrderViewModel(
    private val cartRepository: CartRepository,
    private val couponRepository: CouponRepository,
    private val orderDatabase: OrderDatabase,
) : ViewModel(), OrderEventHandler {
    private val _uiState: MutableLiveData<OrderUiState> = MutableLiveData(OrderUiState())
    val uiState: LiveData<OrderUiState> get() = _uiState

    init {
        viewModelScope.launch {
            initOrderCarts()
            loadCoupons()
        }
    }

    private fun initOrderCarts() {
        val order = orderDatabase.getOrder()
        val state = uiState.value ?: return
        _uiState.value = state.copy(orderCarts = order.list)
    }

    private fun loadCoupons() {
        viewModelScope.launch {
            val transaction = couponRepository.getCoupons()
            transaction.onSuccess { coupons ->
                val state = uiState.value ?: return@launch
                val couponsState = coupons.filter { it.isValidCoupon(state.orderCarts) }
                _uiState.value = (state.copy(couponsState = couponsState))
            }
        }
    }

    override fun toggleCoupon(couponState: CouponState) {
        val state = uiState.value ?: return
        val updateCouponState =
            state.couponsState.map { couponItem ->
                if (couponItem.coupon == couponState.coupon) {
                    couponItem.copy(checked = !couponItem.coupon.checked)
                } else {
                    couponItem.copy(checked = false)
                }
            }
        _uiState.value = state.copy(couponsState = updateCouponState)
    }

    fun payment() {
        viewModelScope.launch {
            val state = uiState.value ?: return@launch
            val transaction = cartRepository.makeOrder(Order(state.orderCarts))
            transaction.onSuccess {
                Log.d("crong", "order complete")
            }
        }
    }
}
