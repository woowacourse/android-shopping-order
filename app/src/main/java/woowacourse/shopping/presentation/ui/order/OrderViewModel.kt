package woowacourse.shopping.presentation.ui.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Order
import woowacourse.shopping.domain.model.coupon.CouponState
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository

class OrderViewModel(
    private val cartRepository: CartRepository,
    private val couponRepository: CouponRepository,
    private val order: Order,
) : ViewModel(), OrderEventHandler {
    private val _uiState: MutableLiveData<OrderUiState> = MutableLiveData(OrderUiState())
    val uiState: LiveData<OrderUiState> get() = _uiState

    val totalPrice: LiveData<Int> =
        uiState.switchMap {
            MutableLiveData(it.totalPrice)
        }

    /*private val _navigateAction: MutableLiveData<Event<PaymentNavigateAction>> =
        MutableLiveData(null)
    val navigateAction: LiveData<Event<PaymentNavigateAction>> get() = _navigateAction*/
    val title = "주문 금액"

    @Suppress("ktlint:standard:property-naming")
    val FAKE_ITEM =
        CartItem(
            id = 10103,
            productId = 10,
            productName = "퓨마",
            price = 10000,
            quantity = 5,
            imgUrl = "https://sitem.ssgcdn.com/47/78/22/item/1000031227847_i1_750.jpg",
            isChecked = true,
        )

    init {
        loadCoupons()
        initOrderCarts()
    }

    private fun initOrderCarts() {
        /*val cartsWrapper =
            requireNotNull(savedStateHandle.get<CartsWrapper>(PaymentActivity.PUT_EXTRA_CARTS_WRAPPER_KEY))*/

        val state = uiState.value ?: return
        _uiState.value = state.copy(orderCarts = listOf(FAKE_ITEM))
        Log.d("crong", "initOrderCarts: ${uiState.value}")
    }

    private fun loadCoupons() {
        viewModelScope.launch {
            val transaction = couponRepository.getCoupons()
            Log.d("crong", "loadCoupons: $transaction")
            transaction.onSuccess { coupons ->
                Log.d("crong", "loadCoupons: $coupons")
                val state = uiState.value ?: return@launch
                Log.d("crong", "loadCoupons: $state")
                val couponsState = coupons.filter { it.isValidCoupon(state.orderCarts) }
                Log.d("crong", "loadCoupons: $couponsState")
                _uiState.value = (state.copy(couponsState = couponsState))
                Log.d("crong", "loadCoupons: ${uiState.value}")
            }
            /*couponRepository.getCoupons().onSuccess { coupons ->
                Log.d("crong", "loadCoupons: $coupons")
                val state = uiState.value ?: return@launch
                val couponsState = coupons.filter { it.isValidCoupon(state.orderCarts) }
                _uiState.postValue(state.copy(couponsState = couponsState))
            }*/
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

            /*orderRepository.insertOrder(state.orderCarts.map { it.id }).onSuccess {
                showMessage(PaymentMessage.PaymentSuccessMessage)
                _navigateAction.emit(PaymentNavigateAction.NavigateToProductList)
            }*/

            val transaction = cartRepository.makeOrder(Order(state.orderCarts))
            transaction.onSuccess {
                Log.d("crong", "order complete")
                // _navigateAction.emit(PaymentNavigateAction.NavigateToProductList)
            }
        }
    }
}
