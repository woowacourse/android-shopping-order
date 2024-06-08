package woowacourse.shopping.view.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.CartItemDomain
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.util.Event
import woowacourse.shopping.view.order.CouponViewItem.CouponItem
import woowacourse.shopping.view.state.OrderUiEvent

class OrderViewModel(
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
    private val cartItems: List<CartItemDomain>,
) : ViewModel(), CouponItemClickListener {
    private val _orderUiState: MutableLiveData<OrderUiState> =
        MutableLiveData(OrderUiState())
    val orderUiState: LiveData<OrderUiState>
        get() = _orderUiState

    private val _couponUiEvent: MutableLiveData<Event<OrderUiEvent>> =
        MutableLiveData()
    val orderUiEvent: LiveData<Event<OrderUiEvent>>
        get() = _couponUiEvent

    val totalPrice: LiveData<Int> = orderUiState.map {
        it.orderPrice - it.discount
    }.distinctUntilChanged()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
        showError()
    }

    init {
        println(cartItems)
        loadPage()
    }

    override fun changeCouponSelection(
        isSelected: Boolean,
        couponId: Int,
    ) {
        if (isSelected) {
            selectCoupon(couponId)
        } else {
            cancelCoupon(couponId)
        }
    }

    private fun selectCoupon(couponId: Int) {
        _orderUiState.value = orderUiState.value?.applyCoupon(couponId)
    }

    private fun cancelCoupon(couponId: Int) {
        _orderUiState.value = orderUiState.value?.cancelCoupon(couponId)
    }

    fun makeOrder() {
        viewModelScope.launch(coroutineExceptionHandler) {
            orderRepository.postOrder(
                cartItems.map(CartItemDomain::cartItemId)
            ).onSuccess {
                _couponUiEvent.value = Event(OrderUiEvent.NavigateBackToHome)
            }.onFailure {
                showError()
            }
        }
    }

    private fun showError() {
        _couponUiEvent.value = Event(OrderUiEvent.Error)
    }

    private fun loadPage() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val coupons = couponRepository.getCoupons().getOrThrow()
            val couponViewItems = coupons.map(::CouponItem)
            _orderUiState.value = orderUiState.value?.copy(
                isLoading = false,
                cartItems = cartItems,
                coupons = couponViewItems,
                orderPrice = cartItems.sumOf { it.totalPrice() } + 3000
            )?.filterAvailableCoupons()
        }
    }
}
