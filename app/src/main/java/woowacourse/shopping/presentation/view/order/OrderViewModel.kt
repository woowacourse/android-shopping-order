package woowacourse.shopping.presentation.view.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.OrderSummary
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.presentation.model.CartItemUiModel
import woowacourse.shopping.presentation.model.CouponUiModel
import woowacourse.shopping.presentation.model.toDomain
import woowacourse.shopping.presentation.model.toUiModel
import woowacourse.shopping.presentation.util.MutableSingleLiveData
import woowacourse.shopping.presentation.util.SingleLiveData

class OrderViewModel(
    private val orderRepository: OrderRepository,
    private val couponRepository: CouponRepository,
) : ViewModel() {
    private var selectedItems = emptyList<CartItemUiModel>()

    private val _couponList = MutableLiveData<List<CouponUiModel>>()
    val couponList: LiveData<List<CouponUiModel>> = _couponList

    private val _selectedCoupon = MutableLiveData<CouponUiModel?>()
    val selectedCoupon: LiveData<CouponUiModel?> = _selectedCoupon

    private val _orderSummary = MutableLiveData<OrderSummary>()
    val orderSummary: LiveData<OrderSummary> = _orderSummary

    private val _orderSuccessEvent = MutableSingleLiveData<Unit>()
    val orderSuccessEvent: SingleLiveData<Unit> = _orderSuccessEvent

    private val _toastEvent = MutableSingleLiveData<OrderEvent>()
    val toastEvent: SingleLiveData<OrderEvent> = _toastEvent

    fun loadCoupons() {
        viewModelScope.launch {
            couponRepository
                .getAvailableCoupons(selectedItems.map { it.toDomain() })
                .onSuccess { coupon ->
                    _couponList.value = coupon.map { it.toUiModel() }
                }.onFailure {
                    _couponList.value = emptyList()
                    _toastEvent.setValue(OrderEvent.LOAD_COUPONS_FAILURE)
                }
        }
    }

    fun selectCoupon(coupon: CouponUiModel) {
        val isSelected = _selectedCoupon.value?.id == coupon.id

        _selectedCoupon.value = if (isSelected) null else coupon
        _couponList.value =
            _couponList.value?.map {
                it.copy(isSelected = if (isSelected) false else it.id == coupon.id)
            }
    }

    fun setSelectedItems(items: List<CartItemUiModel>) {
        selectedItems = items.toList()
    }

    fun calculateOrderSummary(coupon: CouponUiModel?) {
        val orderAmount = selectedItems.sumOf { it.totalPrice }

        val selectedCoupon =
            coupon?.id?.let { id ->
                couponRepository.getCouponById(id).getOrNull()
            }

        val couponAmount =
            selectedCoupon?.let { it.calculateDiscountAmount(selectedItems.map { it.toDomain() }) }

        _orderSummary.value =
            OrderSummary(
                orderAmount = orderAmount.toLong(),
                couponAmount = couponAmount ?: 0,
                shippingFee = if (selectedCoupon is Coupon.FreeShippingCoupon) 0 else 3000,
            )
    }

    fun order() {
        viewModelScope.launch {
            orderRepository
                .order(selectedItems.map { it.toDomain() })
                .onSuccess {
                    _orderSuccessEvent.postValue(Unit)
                    _toastEvent.setValue(OrderEvent.ORDER_SUCCESS)
                }.onFailure {
                    _toastEvent.setValue(OrderEvent.ORDER_FAILURE)
                }
        }
    }

    companion object {
        fun factory(
            orderRepository: OrderRepository,
            couponRepository: CouponRepository,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T = OrderViewModel(orderRepository, couponRepository) as T
            }
    }
}
