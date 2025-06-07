package woowacourse.shopping.presentation.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.R
import woowacourse.shopping.domain.model.OrderPriceSummary
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.domain.usecase.GetAvailableCouponUseCase
import woowacourse.shopping.presentation.Extra.KEY_SELECT_ITEMS
import woowacourse.shopping.presentation.SingleLiveData
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.model.CouponUiModel
import woowacourse.shopping.presentation.model.toDomain
import woowacourse.shopping.presentation.model.toPresentation

class OrderViewModel(
    savedStateHandle: SavedStateHandle,
    private val getAvailableCouponUseCase: GetAvailableCouponUseCase,
    private val orderRepository: OrderRepository,
) : ViewModel() {
    private val _coupons: MutableLiveData<List<CouponUiModel>> = MutableLiveData()
    val coupons: LiveData<List<CouponUiModel>> = _coupons

    private val _orderSummary: MutableLiveData<OrderPriceSummary> = MutableLiveData()
    val orderSummary: LiveData<OrderPriceSummary> = _orderSummary

    private val _orderSuccessEvent = SingleLiveData<Boolean>()
    val orderSuccessEvent: LiveData<Boolean> = _orderSuccessEvent

    private val _toastMessage = SingleLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage

    init {
        val initialItems =
            savedStateHandle.get<ArrayList<CartItemUiModel>>(KEY_SELECT_ITEMS) ?: emptyList()
        _orderSummary.value = createOrderSummary(initialItems)
        fetchData()
    }

    fun selectCoupon(selectedCoupon: CouponUiModel) {
        val updatedCoupons = toggleCouponSelection(_coupons.value.orEmpty(), selectedCoupon)
        _coupons.value = updatedCoupons

        val order = orderSummary.value ?: return
        val removedCouponOrder = order.removeCoupon()
        val selected = updatedCoupons.find { it.isSelected }
        _orderSummary.value = selected
            ?.let { removedCouponOrder.applyCoupon(it.toDomain()) }
            ?: removedCouponOrder
    }

    fun order() {
        val summary = orderSummary.value ?: return
        val orderIds = summary.cartItems.map { it.cartId }

        viewModelScope.launch {
            orderRepository
                .order(orderIds)
                .onSuccess {
                    _toastMessage.value = R.string.order_toast_coupon_order_success
                    _orderSuccessEvent.value = true
                }.onFailure {
                    _toastMessage.value = R.string.order_toast_coupon_order_fail
                    _orderSuccessEvent.value = false
                }
        }
    }

    private fun fetchData() {
        val summary = orderSummary.value ?: return
        val orderPrice = summary.productTotalPrice
        val selectedItems = summary.cartItems

        viewModelScope.launch {
            getAvailableCouponUseCase(orderPrice, selectedItems)
                .onSuccess { coupons ->
                    _coupons.value = coupons.map { it.toPresentation() }
                }.onFailure {
                    _toastMessage.value = R.string.order_toast_coupon_load_fail
                }
        }
    }

    private fun toggleCouponSelection(
        currentCoupons: List<CouponUiModel>,
        selectedCoupon: CouponUiModel,
    ): List<CouponUiModel> {
        val isCurrentlySelected =
            currentCoupons.any { it.code == selectedCoupon.code && it.isSelected }

        return currentCoupons.map { coupon ->
            coupon.copy(isSelected = if (isCurrentlySelected) false else coupon.code == selectedCoupon.code)
        }
    }

    private fun createOrderSummary(initialItems: List<CartItemUiModel>): OrderPriceSummary =
        OrderPriceSummary(
            productTotalPrice = initialItems.sumOf { it.totalPrice },
            cartItems = initialItems.map { it.toDomain() },
        )
}
