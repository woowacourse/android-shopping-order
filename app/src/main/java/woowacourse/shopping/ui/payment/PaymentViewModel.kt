package woowacourse.shopping.ui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApp
import woowacourse.shopping.domain.model.Orders
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.DefaultCouponRepository
import woowacourse.shopping.domain.repository.DefaultOrderRepository
import woowacourse.shopping.domain.repository.OrderRepository
import woowacourse.shopping.ui.model.CouponUi
import woowacourse.shopping.ui.model.toUi
import woowacourse.shopping.ui.payment.event.PaymentError
import woowacourse.shopping.ui.util.MutableSingleLiveData
import woowacourse.shopping.ui.util.SingleLiveData
import woowacourse.shopping.ui.util.UniversalViewModelFactory

class PaymentViewModel(
    private val couponRepository: CouponRepository,
    private val orderRepository: OrderRepository,
) : ViewModel(), CouponCheckListener {
    private val _orders: MutableLiveData<Orders> = MutableLiveData()
    val orders: LiveData<Orders> get() = _orders

    private val _loadedCoupons = MutableSingleLiveData<List<CouponUi>>()
    val loadedCoupons: MutableSingleLiveData<List<CouponUi>> = _loadedCoupons

    private val _discountedPrice: MutableLiveData<Int> = MutableLiveData()
    val discountedPrice: LiveData<Int> get() = _discountedPrice

    private val _payEvent: MutableSingleLiveData<Boolean> = MutableSingleLiveData()
    val payEvent: SingleLiveData<Boolean> get() = _payEvent

    private val _error: MutableSingleLiveData<PaymentError> = MutableSingleLiveData()
    val error: SingleLiveData<PaymentError> get() = _error

    fun loadOrders() {
        viewModelScope.launch {
            loadOrderItems()
            loadCoupons()
        }
    }

    private suspend fun loadCoupons() {
        couponRepository.availableCoupons(orders = orders.value ?: Orders.DEFAULT)
            .onSuccess { coupons ->
                _loadedCoupons.setValue(coupons.toUi())
            }
            .onFailure {
                _error.setValue(PaymentError.LoadCoupons)
            }
    }

    private suspend fun loadOrderItems() {
        orderRepository.loadAllOrders()
            .onSuccess { loadedOrders ->
                _orders.value = loadedOrders
            }
            .onFailure {
                _error.setValue(PaymentError.LoadOrders)
            }
    }

    override fun onCheck(coupon: CouponUi) {
        viewModelScope.launch {
            couponRepository.discountAmount(coupon.id, orders = orders.value ?: Orders.DEFAULT)
                .onSuccess { discountAmount ->
                    _discountedPrice.value = discountAmount
                    changeCheckedCoupons(coupon)
                }.onFailure {
                    _error.setValue(PaymentError.DiscountAmount)
                }
        }
    }

    private fun changeCheckedCoupons(coupon: CouponUi) {
        _loadedCoupons.setValue(
            loadedCoupons.getValue().orEmpty().map { couponUi ->
                if (couponUi.id == coupon.id) {
                    couponUi.copy(isChecked = true)
                } else {
                    couponUi.copy(isChecked = false)
                }
            },
        )
    }

    fun pay() {
        viewModelScope.launch {
            orderRepository.order()
                .onSuccess {
                    _payEvent.setValue(true)
                }
                .onFailure {
                    _error.setValue(PaymentError.Order)
                }
        }
    }

    companion object {
        private const val TAG = "PaymentViewModel"

        fun factory(): UniversalViewModelFactory =
            UniversalViewModelFactory {
                PaymentViewModel(
                    couponRepository =
                        DefaultCouponRepository(
                            ShoppingApp.couponSource,
                        ),
                    orderRepository =
                        DefaultOrderRepository(
                            ShoppingApp.orderSource,
                            ShoppingApp.cartSource,
                        ),
                )
            }
    }
}
