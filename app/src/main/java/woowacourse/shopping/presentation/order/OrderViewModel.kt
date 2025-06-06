package woowacourse.shopping.presentation.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.R
import woowacourse.shopping.domain.model.OrderPriceSummary
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
) : ViewModel() {
    private val _coupons: MutableLiveData<List<CouponUiModel>> = MutableLiveData()
    val coupons: LiveData<List<CouponUiModel>> = _coupons
    private val _orderSummary: MutableLiveData<OrderPriceSummary> = MutableLiveData()
    val orderSummary: LiveData<OrderPriceSummary> = _orderSummary
    private val _toastMessage = SingleLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage

    private val initialItems =
        savedStateHandle.get<ArrayList<CartItemUiModel>>(KEY_SELECT_ITEMS) ?: emptyList()

    init {
        _orderSummary.value =
            OrderPriceSummary(
                productTotalPrice = initialItems.sumOf { it.totalPrice },
                cartItems = initialItems,
            )
        fetchData()
    }

    private fun fetchData() {
        val orderPrice = orderSummary.value?.productTotalPrice ?: return
        val selectedItems = orderSummary.value?.cartItems ?: return
        viewModelScope.launch {
            getAvailableCouponUseCase(orderPrice, selectedItems.map { it.toDomain() })
                .onSuccess { coupons ->
                    _coupons.value = coupons.map { it.toPresentation() }
                }.onFailure {
                    _toastMessage.value = R.string.order_toast_coupon_load_fail
                }
        }
    }
}
