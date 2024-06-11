package woowacourse.shopping.presentation.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.model.CouponModel
import woowacourse.shopping.presentation.ui.model.toUiModel
import woowacourse.shopping.presentation.util.Event

class OrderViewModel(
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
    private val selectedCartIds: List<Long>,
    val totalPriceWithoutDiscount: Long,
) : ViewModel(), OrderHandler {
    private val _coupons: MutableLiveData<UiState<List<CouponModel>>> = MutableLiveData()
    val coupons: LiveData<UiState<List<CouponModel>>> = _coupons

    private val _discountAmount: MutableLiveData<Int> = MutableLiveData(0)
    val discountAmount: LiveData<Int> = _discountAmount

    val totalPriceWithDiscount: LiveData<Long> =
        discountAmount.map { shippingFee.value?.plus(totalPriceWithoutDiscount)?.minus(it) ?: 0 }

    private val couponsData get() = (coupons.value as? UiState.Success)?.data ?: emptyList()

    private val _shippingFee: MutableLiveData<Int> = MutableLiveData(OrderRepository.SHIPPING_FEE)
    val shippingFee: LiveData<Int> = _shippingFee

    private val _completeOrder: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val completeOrder: LiveData<Event<Boolean>> = _completeOrder

    private val _error = MutableLiveData<Event<OrderError>>()
    val error: LiveData<Event<OrderError>> get() = _error

    init {
        viewModelScope.launch {
            couponRepository.findCoupons(selectedCartIds)
                .map { coupons -> coupons.map { it.toUiModel() } }
                .onSuccess { couponModels ->
                    _coupons.value = UiState.Success(couponModels)
                }
                .onFailure { _error.value = Event(OrderError.CouponsNotFound) }
        }
    }

    override fun onCheckBoxClicked(couponId: Long) {
        val selectedCoupon = couponsData.find { it.id == couponId } ?: return
        val updated =
            couponsData.map {
                if (it.id != couponId) {
                    it.copy(isChecked = false)
                } else {
                    it.copy(isChecked = !it.isChecked)
                }
            }
        _coupons.value = UiState.Success(updated)
        _discountAmount.value = selectedCoupon.discountAmount
    }

    override fun onPayButtonClicked() {
        viewModelScope.launch {
            orderRepository.completeOrder(selectedCartIds)
                .onSuccess { _completeOrder.value = Event(true) }
                .onFailure { _error.value = Event(OrderError.FailToOrder) }
        }
    }

    companion object {
        class Factory(
            private val couponRepository: CouponRepository,
            private val orderRepository: OrderRepository,
            private val selectedCartIds: List<Long>,
            private val totalPriceWithoutDiscount: Long,
        ) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return OrderViewModel(
                    couponRepository,
                    orderRepository,
                    selectedCartIds,
                    totalPriceWithoutDiscount,
                ) as T
            }
        }
    }
}
